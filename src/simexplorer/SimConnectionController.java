package simexplorer;

import java.util.function.BooleanSupplier;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import simexplorer.simcardcloner.SIMCardType;

class SimConnectionController {
    private final SmartCardController smartCardController;
    private final HistoryLogger historyLogger;
    private final ApduService apduService;
    private final ConnectionUiStateController connectionUiStateController;

    private Thread threadWaitForCardAbsent = null;
    private SIMCardType simCardType = SIMCardType.Regular;
    private CardChannel cardChannel;

    SimConnectionController(
            SmartCardController smartCardController,
            HistoryLogger historyLogger,
            ApduService apduService,
            ConnectionUiStateController connectionUiStateController
    ) {
        this.smartCardController = smartCardController;
        this.historyLogger = historyLogger;
        this.apduService = apduService;
        this.connectionUiStateController = connectionUiStateController;
    }

    SIMCardType getSimCardType() {
        return simCardType;
    }

    void initializeDisconnectedState(FileTreeController fileTreeController) {
        connectionUiStateController.onDisconnected(fileTreeController, simCardType);
    }

    void disconnect(FileTreeController fileTreeController) {
        try {
            historyLogger.addEntry("Disconnected");
            smartCardController.disconnect();
            if (smartCardController.isCardPresent()) {
                connectionUiStateController.onDisconnected(fileTreeController, simCardType);
            }
        } catch (CardException ex) {
        }
    }

    void connectToTerminal(
            int terminalNumber,
            FileTreeController fileTreeController,
            BooleanSupplier isDisconnectVisible
    ) {
        try {
            SmartCardController.ConnectionResult connectionResult = smartCardController.connectToTerminal(terminalNumber);
            CardTerminal terminal = connectionResult.getTerminal();
            historyLogger.addEntry("Selected terminal: " + terminal.getName());
            historyLogger.addEntry("Connected");
            System.out.println("card: " + connectionResult.getCard());
            byte[] buffer = connectionResult.getAtr().getBytes();
            historyLogger.addEntry("ATR : " + HistoryLogger.formatBuffer(buffer));
            cardChannel = connectionResult.getCardChannel();
            apduService.setCardChannel(cardChannel);
        } catch (CardException ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), "Err", 0, null);
            return;
        }

        simCardType = connectionUiStateController.onConnected(apduService, fileTreeController);

        if (threadWaitForCardAbsent == null) {
            threadWaitForCardAbsent = smartCardController.startCardPresenceMonitor(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (isDisconnectVisible.getAsBoolean()) {
                                historyLogger.addEntry("Disconnected");
                            }
                            connectionUiStateController.onDisconnected(fileTreeController, simCardType);
                        }
                    },
                    new Runnable() {
                        @Override
                        public void run() {
                        }
                    }
            );
        }
    }
}

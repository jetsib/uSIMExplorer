package simexplorer.files.efTools;

public final class FileTreeDef {

    public static final NodeDef MF = NodeDef.mf("MF", 0x3F00);

    public static final NodeDef ICCID   = NodeDef.ef("ICCID", 0x2FE2);
    public static final NodeDef TELECOM = NodeDef.df("TELECOM", 0x7F10);
    public static final NodeDef DF_GSM  = NodeDef.df("GSM", 0x7F20);

    public static final NodeDef ADF_USIM = NodeDef.adf("USIM", new byte[]{
            (byte)0xA0, 0x00, 0x00, 0x00, (byte)0x87, 0x10, 0x02
    });

    public static final Object[] TELECOM_EF = {
            NodeDef.ef("ADN", 0x6F3A),
            NodeDef.ef("FDN", 0x6F3B),
            NodeDef.ef("SMS", 0x6F3C),
            NodeDef.ef("CCP", 0x6F3D),
            NodeDef.ef("MSISDN", 0x6F40),
            NodeDef.ef("SMSP", 0x6F42),
            NodeDef.ef("SMSS", 0x6F43),
            NodeDef.ef("LND", 0x6F44),
            NodeDef.ef("SDN", 0x6F49),
            NodeDef.ef("EXT1", 0x6F4A),
            NodeDef.ef("EXT2", 0x6F4B),
            NodeDef.ef("EXT3", 0x6F4C)
    };

    public static final Object[] MF_CHILDREN = {
            ICCID,
            TELECOM, TELECOM_EF
    };

    public static final Object[] GSM_EF = {
            NodeDef.ef("IMSI", 0x6F07),
            NodeDef.ef("ACC",  0x6F78),
            NodeDef.ef("GID1", 0x6F3E),
            NodeDef.ef("GID2", 0x6F3F),
            NodeDef.ef("SPN",  0x6F46),

            NodeDef.ef("LP", 0x6F05),
            NodeDef.ef("KC", 0x6F20),
            NodeDef.ef("PLMNSel", 0x6F30),
            NodeDef.ef("HPLMN", 0x6F31),
            NodeDef.ef("FPLMN", 0x6F7B),
            NodeDef.ef("AD", 0x6FAD),
            NodeDef.ef("PHASE", 0x6FAE),
            NodeDef.ef("SST", 0x6F38),
            NodeDef.ef("CBMI", 0x6F45),
            NodeDef.ef("CBMID", 0x6F48),
            NodeDef.ef("BCCH", 0x6F74)
    };

    public static final Object[] USIM_EF = {
            NodeDef.ef("IMSI", 0x6F07),
            NodeDef.ef("ACC",  0x6F78),
            NodeDef.ef("GID1", 0x6F3E),
            NodeDef.ef("GID2", 0x6F3F),
            NodeDef.ef("SPN",  0x6F46),

            NodeDef.ef("UST", 0x6F05),
            NodeDef.ef("PLMNwAcT", 0x6F60),
            NodeDef.ef("OPLMNwAcT", 0x6F61),
            NodeDef.ef("HPLMNwAcT", 0x6F62),
            NodeDef.ef("EHPLMN", 0x6FD9),
            NodeDef.ef("FPLMN", 0x6F7B),
            NodeDef.ef("AD", 0x6FAD)
    };

    private FileTreeDef() {}
}

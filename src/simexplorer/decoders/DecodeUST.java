package simexplorer.decoders;

public class DecodeUST implements DecodeEF {

    private static final String[] N = new String[146];

    static {
        N[1] = "Local Phone Book";
        N[2] = "Fixed Dialling Numbers (FDN)";
        N[3] = "Extension 2";
        N[4] = "Service Dialling Numbers (SDN)";
        N[5] = "Extension 3";
        N[6] = "Barred Dialling Numbers (BDN)";
        N[7] = "Extension 4";
        N[8] = "Outgoing Call Information (OCI and OCT)";
        N[9] = "Incoming Call Information (ICI and ICT)";
        N[10] = "Short Message Storage (SMS)";
        N[11] = "Short Message Status Reports (SMSR)";
        N[12] = "Short Message Service Parameters (SMSP)";
        N[13] = "Advice of Charge (AoC)";
        N[14] = "Capability Configuration Parameters 2 (CCP2)";
        N[15] = "Cell Broadcast Message Identifier";
        N[16] = "Cell Broadcast Message Identifier Ranges";
        N[17] = "Group Identifier Level 1";
        N[18] = "Group Identifier Level 2";
        N[19] = "Service Provider Name";
        N[20] = "User controlled PLMN selector with Access Technology";
        N[21] = "MSISDN";
        N[22] = "Image (IMG)";
        N[23] = "Support of Localised Service Areas (SoLSA)";
        N[24] = "Enhanced Multi-Level Precedence and Pre-emption Service";
        N[25] = "Automatic Answer for eMLPP";
        N[26] = "RFU";
        N[27] = "GSM Access";
        N[28] = "Data download via SMS-PP";
        N[29] = "Data download via SMS-CB";
        N[30] = "Call Control by USIM";
        N[31] = "MO-SMS Control by USIM";
        N[32] = "RUN AT COMMAND command";
        N[33] = "shall be set to '1'";
        N[34] = "Enabled Services Table";
        N[35] = "APN Control List (ACL)";
        N[36] = "Depersonalisation Control Keys";
        N[37] = "Co-operative Network List";
        N[38] = "GSM security context";
        N[39] = "CPBCCH Information";
        N[40] = "Investigation Scan";
        N[41] = "MexE";
        N[42] = "Operator controlled PLMN selector with Access Technology";
        N[43] = "HPLMN selector with Access Technology";
        N[44] = "Extension 5";
        N[45] = "PLMN Network Name";
        N[46] = "Operator PLMN List";
        N[47] = "Mailbox Dialling Numbers";
        N[48] = "Message Waiting Indication Status";
        N[49] = "Call Forwarding Indication Status";
        N[50] = "Reserved and shall be ignored";
        N[51] = "Service Provider Display Information";
        N[52] = "Multimedia Messaging Service (MMS)";
        N[53] = "Extension 8";
        N[54] = "Call control on GPRS by USIM";
        N[55] = "MMS User Connectivity Parameters";
        N[56] = "Network's indication of alerting in the MS (NIA)";
        N[57] = "VGCS Group Identifier List (EF_VGCS and EF_VGCSS)";
        N[58] = "VBS Group Identifier List (EF_VBS and EF_VBSS)";
        N[59] = "Pseudonym";
        N[60] = "User Controlled PLMN selector for I-WLAN access";
        N[61] = "Operator Controlled PLMN selector for I-WLAN access";
        N[62] = "User controlled WSID list";
        N[63] = "Operator controlled WSID list";
        N[64] = "VGCS security";
        N[65] = "VBS security";
        N[66] = "WLAN Reauthentication Identity";
        N[67] = "Multimedia Messages Storage";
        N[68] = "Generic Bootstrapping Architecture (GBA)";
        N[69] = "MBMS security";
        N[70] = "Data download via USSD and USSD application mode";
        N[71] = "Equivalent HPLMN";
        N[72] = "Additional TERMINAL PROFILE after UICC activation";
        N[73] = "Equivalent HPLMN Presentation Indication";
        N[74] = "Last RPLMN Selection Indication";
        N[75] = "OMA BCAST Smart Card Profile";
        N[76] = "GBA-based Local Key Establishment Mechanism";
        N[77] = "Terminal Applications";
        N[78] = "Service Provider Name Icon";
        N[79] = "PLMN Network Name Icon";
        N[80] = "Connectivity Parameters for USIM IP connections";
        N[81] = "Home I-WLAN Specific Identifier List";
        N[82] = "I-WLAN Equivalent HPLMN Presentation Indication";
        N[83] = "I-WLAN HPLMN Priority Indication";
        N[84] = "I-WLAN Last Registered PLMN";
        N[85] = "EPS Mobility Management Information";
        N[86] = "Allowed CSG Lists and corresponding indications";
        N[87] = "Call control on EPS PDN connection by USIM";
        N[88] = "HPLMN Direct Access";
        N[89] = "eCall Data";
        N[90] = "Operator CSG Lists and corresponding indications";
        N[91] = "Support for SM-over-IP";
        N[92] = "Support of CSG Display Control";
        N[93] = "Communication Control for IMS by USIM";
        N[94] = "Extended Terminal Applications";
        N[95] = "Support of UICC access to IMS";
        N[96] = "Non-Access Stratum configuration by USIM";
        N[97] = "PWS configuration by USIM";
        N[98] = "RFU";
        N[99] = "URI support by UICC";
        N[100] = "Extended EARFCN support";
        N[101] = "ProSe";
        N[102] = "USAT Application Pairing";
        N[103] = "Media Type support";
        N[104] = "IMS call disconnection cause";
        N[105] = "URI support for MO SHORT MESSAGE CONTROL";
        N[106] = "ePDG configuration Information support";
        N[107] = "ePDG configuration Information configured";
        N[108] = "ACDC support";
        N[109] = "Mission Critical Services";
        N[110] = "ePDG configuration Information for Emergency Service support";
        N[111] = "ePDG configuration Information for Emergency Service configured";
        N[112] = "eCall Data over IMS";
        N[113] = "URI support for SMS-PP DOWNLOAD";
        N[114] = "From Preferred";
        N[115] = "IMS configuration data";
        N[116] = "TV configuration";
        N[117] = "3GPP PS Data Off";
        N[118] = "3GPP PS Data Off Service List";
        N[119] = "V2X";
        N[120] = "XCAP Configuration Data";
        N[121] = "EARFCN list for MTC/NB-IOT UEs";
        N[122] = "5GS Mobility Management Information";
        N[123] = "5G Security Parameters";
        N[124] = "Subscription identifier privacy support";
        N[125] = "SUCI calculation by the USIM";
        N[126] = "UAC Access Identities support";
        N[127] = "Control plane-based steering of UE in VPLMN";
        N[128] = "Call control on PDU Session by USIM";
        N[129] = "5GS Operator PLMN List";
        N[130] = "Support for SUPI of type NSI or GLI or GCI";
        N[131] = "3GPP PS Data Off separate Home and Roaming lists";
        N[132] = "Support for URSP by USIM";
        N[133] = "5G Security Parameters extended";
        N[134] = "MuD and MiD configuration data";
        N[135] = "Support for Trusted non-3GPP access networks by USIM";
        N[136] = "Support for multiple records of NAS security context storage for multiple registration";
        N[137] = "Pre-configured CAG information list";
        N[138] = "SOR-CMCI storage in USIM";
        N[139] = "5G ProSe";
        N[140] = "Storage of disaster roaming information in USIM";
        N[141] = "Pre-configured eDRX parameters";
        N[142] = "5G NSWO support";
        N[143] = "PWS configuration for SNPN in USIM";
        N[144] = "Multiplier Coefficient for Higher Priority PLMN search via NG-RAN satellite access";
        N[145] = "K_AUSF derivation configuration";
    }

    @Override
    public String decode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "";

        StringBuilder sb = new StringBuilder();
        int service = 1;

        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i] & 0xFF;
            for (int bit = 0; bit < 8; bit++) {
                if ((b & (1 << bit)) != 0) {
                    String name = (service < N.length && N[service] != null)
                            ? N[service]
                            : "Unknown service";
                    sb.append(service)
                            .append(" - ")
                            .append(name)
                            .append("\n");
                }
                service++;
            }
        }

        return sb.toString().trim();
    }
}

package org.cyberiantiger.telnet;

import java.io.IOException;
import java.io.OutputStream;

public interface TelnetOption extends BinaryData {

    /**
     * Top Level Telnet Stuff
     */
    public static final int SE = 240;
    public static final int NOP = 241;
    public static final int DM = 242;
    public static final int BRK = 243;
    public static final int IP = 244;
    public static final int AO = 245;
    public static final int AYT = 246;
    public static final int EC = 247;
    public static final int EL = 248;
    public static final int GA = 249;
    public static final int SB = 250;
    public static final int WILL = 251;
    public static final int WONT = 252;
    public static final int DO = 253;
    public static final int DONT = 254;
    public static final int IAC = 255;

    /**
     * Telnet Option Types
     */
    // TOPT_BIN   Binary Transmission                 0  Std   Rec     856  27
    public static final int TOPT_BIN = 0;
    // TOPT_ECHO  Echo                                1  Std   Rec     857  28
    public static final int TOPT_ECHO = 1;
    // TOPT_RECN  Reconnection                        2  Prop  Ele     ...
    public static final int TOPT_RECN = 2;
    // TOPT_SUPP  Suppress Go Ahead                   3  Std   Rec     858  29
    public static final int TOPT_SUPP = 3;
    // TOPT_APRX  Approx Message Size Negotiation     4  Prop  Ele     ...
    public static final int TOPT_APRX = 4;
    // TOPT_STAT  Status                              5  Std   Rec     859  30
    public static final int TOPT_STAT = 5;
    // TOPT_TIM   Timing Mark                         6  Std   Rec     860  31
    public static final int TOPT_TIM = 6;
    // TOPT_REM   Remote Controlled Trans and Echo    7  Prop  Ele     726
    public static final int TOPT_REM = 7;
    // TOPT_OLW   Output Line Width                   8  Prop  Ele     ...
    public static final int TOPT_OLW = 8;
    // TOPT_OPS   Output Page Size                    9  Prop  Ele     ...
    public static final int TOPT_OPS = 9;
    // TOPT_OCRD  Output Carriage-Return Disposition 10  Hist  Ele     652    *
    public static final int TOPT_OCRD = 10;
    // TOPT_OHT   Output Horizontal Tabstops         11  Hist  Ele     653    *
    public static final int TOPT_OHT = 11;
    // TOPT_OHTD  Output Horizontal Tab Disposition  12  Hist  Ele     654    *
    public static final int TOPT_OHTD = 12;
    // TOPT_OFD   Output Formfeed Disposition        13  Hist  Ele     655    *
    public static final int TOPT_OFD = 13;
    // TOPT_OVT   Output Vertical Tabstops           14  Hist  Ele     656    *
    public static final int TOPT_OVT = 14;
    // TOPT_OVTD  Output Vertical Tab Disposition    15  Hist  Ele     657    *
    public static final int TOPT_OVTD = 15;
    // TOPT_OLD   Output Linefeed Disposition        16  Hist  Ele     658    *
    public static final int TOPT_OLD = 16;
    // TOPT_EXT   Extended ASCII                     17  Prop  Ele     698
    public static final int TOPT_EXT = 17;
    // TOPT_LOGO  Logout                             18  Prop  Ele     727
    public static final int TOPT_LOGO = 18;
    // TOPT_BYTE  Byte Macro                         19  Prop  Ele     735
    public static final int TOPT_BYTE = 19;
    // TOPT_DATA  Data Entry Terminal                20  Prop  Ele    1043
    public static final int TOPT_DATA = 20;
    // TOPT_SUP   SUPDUP                             21  Prop  Ele     736
    public static final int TOPT_SUP = 21;
    // TOPT_SUPO  SUPDUP Output                      22  Prop  Ele     749
    public static final int TOPT_SUPO = 22;
    // TOPT_SNDL  Send Location                      23  Prop  Ele     779
    public static final int TOPT_SNDL = 23;
    // TOPT_TERM  Terminal Type                      24  Prop  Ele    1091
    public static final int TOPT_TERM = 24;
    // TOPT_EOR   End of Record                      25  Prop  Ele     885
    public static final int TOPT_EOR = 25;
    // TOPT_TACACS  TACACS User Identification       26  Prop  Ele     927
    public static final int TOPT_TACACS = 26;
    // TOPT_OM    Output Marking                     27  Prop  Ele     933
    public static final int TOPT_OM = 27;
    // TOPT_TLN   Terminal Location Number           28  Prop  Ele     946
    public static final int TOPT_TLN = 28;
    // TOPT_3270  Telnet 3270 Regime                 29  Prop  Ele    1041
    public static final int TOPT_3270 = 29;
    // TOPT_X.3   X.3 PAD                            30  Prop  Ele    1053
    public static final int TOPT_X_3 = 30;
    // TOPT_NAWS  Negotiate About Window Size        31  Prop  Ele    1073
    public static final int TOPT_NAWS = 31;
    // TOPT_TS    Terminal Speed                     32  Prop  Ele    1079
    public static final int TOPT_TS = 32;
    // TOPT_RFC   Remote Flow Control                33  Prop  Ele    1372
    public static final int TOPT_RFC = 33;
    // TOPT_LINE  Linemode                           34  Draft Ele    1184
    public static final int TOPT_LINE = 34;
    // TOPT_XDL   X Display Location                 35  Prop  Ele    1096
    public static final int TOPT_XDL = 35;
    // TOPT_ENVIR Telnet Environment Option          36  Hist  Not    1408
    //public static final int TOPT_ENVIR = 36;
    // TOPT_AUTH  Telnet Authentication Option       37  Exp   Ele    1416
    public static final int TOPT_AUTH = 37;
    // TOPT_ENVIR Telnet Environment Option          39  Prop  Ele    1572
    public static final int TOPT_ENVIR = 39;
    // TOPT_TN3270E TN3270 Enhancements              40  Draft Ele    2355    *
    public static final int TOPT_TN3270E = 40;
    // TOPT_XAUTH  Telnet XAUTH                      41  Exp
    public static final int TOPT_XAUTH = 41;
    // TOPT_CHARSET Telnet CHARSET                   42  Exp          2066
    public static final int TOPT_CHARSET = 42;
    // TOPT_RSP   Telnet Remote Serial Port          43  Exp
    public static final int TOPT_RSP = 43;
    // TOPT_COMPORT Telnet Com Port Control          44  Exp          2217
    public static final int TOPT_COMPORT = 44;
    // TOPT_SLE   Telnet Suppress Local Echo         45  Exp                  *
    public static final int TOPT_SLE = 45;
    // TOPT_STARTTLS Telnet Start TLS                46  Exp                  *
    public static final int TOPT_STARTTLS = 46;
    // TOPT_KERMIT   Telnet KERMIT                   47  Exp                  *
    public static final int TOPT_KERMIT = 47;
    // TOPT_SEND-URL Send-URL                        48  Exp                  *
    public static final int TOPT_SEND_URL = 48;
    // TOPT_EXTOP Extended-Options-List             255  Std   Rec     861  32
    public static final int TOPT_EXTOP = 255;

    // First Byte in Subnegotation: IAC SB <OPTION> <SEND/IS> <DATA> IAC SE
    public static final int IS = 0;
    public static final int SEND = 1;

    public int getOption();
}

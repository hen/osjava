package org.cyberiantiger.telnet;

public abstract class AbstractTelnetOption implements TelnetOption {

    protected int option;

    public AbstractTelnetOption(int option) {
	this.option = option;
    }

    public int getOption() {
	return option;
    }

    public byte[] getBytes() {
	return new byte[] { (byte) IAC, (byte) option };
    }

    public String toString() {
	return "IAC " + commandToString(option);
    }

    protected String commandToString(int command) {
	switch(command) {
	    case SE:	return "SE";
	    case NOP:	return "NOP";
	    case DM:	return "DM";
	    case BRK:	return "BRK";
	    case IP:	return "IP";
	    case AO:	return "AO";
	    case AYT:	return "AYT";
	    case EC:	return "EC";
	    case EL:	return "EL";
	    case GA:	return "GA";
	    case SB: 	return "SB";
	    case WILL:	return "WILL";
	    case WONT:	return "WONT";
	    case DO:	return "DO";
	    case DONT:	return "DONT";
	    case IAC:	return "IAC";
	    default: return "<" + command + ">";
	}
    }

    protected String optionToString(int option) {
	switch(option) {
	    case TOPT_BIN: return "TOPT_BIN";
	    case TOPT_ECHO: return "TOPT_ECHO";
	    case TOPT_RECN: return "TOPT_RECN";
	    case TOPT_SUPP: return "TOPT_SUPP";
	    case TOPT_APRX: return "TOPT_APRX";
	    case TOPT_STAT: return "TOPT_STAT";
	    case TOPT_TIM: return "TOPT_TIM";
	    case TOPT_REM: return "TOPT_REM";
	    case TOPT_OLW: return "TOPT_OLW";
	    case TOPT_OPS: return "TOPT_OPS";
	    case TOPT_OCRD: return "TOPT_OCRD";
	    case TOPT_OHT: return "TOPT_OHT";
	    case TOPT_OHTD: return "TOPT_OHTD";
	    case TOPT_OFD: return "TOPT_OFD";
	    case TOPT_OVT: return "TOPT_OVT";
	    case TOPT_OVTD: return "TOPT_OVTD";
	    case TOPT_OLD: return "TOPT_OLD";
	    case TOPT_EXT: return "TOPT_EXT";
	    case TOPT_LOGO: return "TOPT_LOGO";
	    case TOPT_BYTE: return "TOPT_BYTE";
	    case TOPT_DATA: return "TOPT_DATA";
	    case TOPT_SUP: return "TOPT_SUP";
	    case TOPT_SUPO: return "TOPT_SUPO";
	    case TOPT_SNDL: return "TOPT_SNDL";
	    case TOPT_TERM: return "TOPT_TERM";
	    case TOPT_EOR: return "TOPT_EOR";
	    case TOPT_TACACS: return "TOPT_TACACS";
	    case TOPT_OM: return "TOPT_OM";
	    case TOPT_TLN: return "TOPT_TLN";
	    case TOPT_3270: return "TOPT_3270";
	    case TOPT_X_3: return "TOPT_X_3";
	    case TOPT_NAWS: return "TOPT_NAWS";
	    case TOPT_TS: return "TOPT_TS";
	    case TOPT_RFC: return "TOPT_RFC";
	    case TOPT_LINE: return "TOPT_LINE";
	    case TOPT_XDL: return "TOPT_XDL";
	    case TOPT_AUTH: return "TOPT_AUTH";
	    case TOPT_ENVIR: return "TOPT_ENVIR";
	    case TOPT_TN3270E: return "TOPT_TN3270E";
	    case TOPT_XAUTH: return "TOPT_XAUTH";
	    case TOPT_CHARSET: return "TOPT_CHARSET";
	    case TOPT_RSP: return "TOPT_RSP";
	    case TOPT_COMPORT: return "TOPT_COMPORT";
	    case TOPT_SLE: return "TOPT_SLE";
	    case TOPT_STARTTLS: return "TOPT_STARTTLS";
	    case TOPT_KERMIT: return "TOPT_KERMIT";
	    case TOPT_SEND_URL: return "TOPT_SEND_URL";
	    case TOPT_EXTOP: return "TOPT_EXTOP";
	    default: return "<" + option + ">";
	}
    }

    protected String dataToString(byte[] data) {
	return "\"" + new String(data) + "\"";
    }
}

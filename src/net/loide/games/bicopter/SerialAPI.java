package net.loide.games.bicopter;

public class SerialAPI {

	// Multiwii Serial Protocol 0 
	private byte MSP_VERSION		= 0;
	private byte MSP_IDENT          = 100;   //out message         multitype + multiwii version + protocol version + capability variable
	private byte MSP_STATUS         = 101;   //out message         cycletime & errors_count & sensor present & box activation

	private byte MSP_SET_RAW_RC     = (byte) 200;   //in message          8 rc chan

	private int THROTTLE;
	private int PITCH;
	private int ROLL;
	private int YAW;
	private int AUX1;
	private int AUX2;
	private int AUX3;
	private int AUX4;

	public void setTHROTTLE(int tHROTTLE) {
		THROTTLE = tHROTTLE;
	}
	public void setPITCH(int pITCH) {
		PITCH = pITCH;
	}
	public void setROLL(int rOLL) {
		ROLL = rOLL;
	}
	public void setYAW(int yAW) {
		YAW = yAW;
	}
	public void setAUX1(int aUX1) {
		AUX1 = aUX1;
	}
	public void setAUX2(int aUX2) {
		AUX2 = aUX2;
	}
	public void setAUX3(int aUX3) {
		AUX3 = aUX3;
	}
	public void setAUX4(int aUX4) {
		AUX4 = aUX4;
	}

	public String computeData(){
		byte comando[] = new byte[22];
		byte checksum = 0;
		
		comando[0] = (byte) "$".charAt(0);
		comando[1] = (byte) "M".charAt(0);
		comando[2] = (byte) "<".charAt(0);
		comando[3] = 16;
		comando[4] = MSP_SET_RAW_RC;
		
		/* RC alias 
		#define ROLL       0
		#define PITCH      1
		#define YAW        2
		#define THROTTLE   3
		#define AUX1       4
		#define AUX2       5
		#define AUX3       6
		#define AUX4       7
		*/
		
		// ROLL
		comando[5]  = (byte) (ROLL & 0xff);
		comando[6]  = (byte) (ROLL >> 8);
		
		// PITCH
		comando[7]  = (byte) (PITCH & 0xff);
		comando[8]  = (byte) (PITCH >> 8);

		// YAW
		comando[9]  = (byte) (YAW & 0xff);
		comando[10] = (byte) (YAW >> 8);

		// THROTTLE
		comando[11] = (byte) (THROTTLE & 0xff);
		comando[12] = (byte) (THROTTLE >> 8);

		// AUX1
		comando[13] = (byte) (AUX1 & 0xff);
		comando[14] = (byte) (AUX1 >> 8);

		// AUX2
		comando[15] = (byte) (AUX2 & 0xff);
		comando[16] = (byte) (AUX2 >> 8);

		// AUX3
		comando[17] = (byte) (AUX3 & 0xff);
		comando[18] = (byte) (AUX3 >> 8);

		// AUX4
		comando[19] = (byte) (AUX4 & 0xff);
		comando[20] = (byte) (AUX4 >> 8);

		// calculate checksum
		for (int i = 3; i < 21; i++) {
			checksum = (byte) (comando[i] ^ checksum); 
		}	
		comando[21] = checksum;

		// return "kind of" binary string
		return comando.toString();
	}
	
	
/*
request message to multiwii
To request simple data without parameters / send a specific command / inject new parameters in multiwii
messages are formated like this:
$M>[data length][code][data][checksum]
1 octet '$'
1 octet 'M'
1 octet '<'
1 octet [data length]
1 octet [code]
several octets [data]
1 octet [checksum]

[data length] can be 0 in case of no param command

 */
	
}

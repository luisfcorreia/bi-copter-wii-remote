package net.loide.games.bicopter;

public class SerialAPI {

	// Multiwii Serial Protocol 0 
	private int MSP_VERSION		= 0;

	private int MSP_IDENT          = 100;   //out message         multitype + multiwii version + protocol version + capability variable
	private int MSP_STATUS         = 101;   //out message         cycletime & errors_count & sensor present & box activation
	private int MSP_SET_RAW_RC     = 200;   //in message          8 rc chan
/*
	private int MSP_RAW_IMU        = 102;   //out message         9 DOF
	private int MSP_SERVO          = 103;   //out message         8 servos
	private int MSP_MOTOR          = 104;   //out message         8 motors
	private int MSP_RC             = 105;   //out message         8 rc chan
	private int MSP_RAW_GPS        = 106;   //out message         fix, numsat, lat, lon, alt, speed
	private int MSP_COMP_GPS       = 107;   //out message         distance home, direction home
	private int MSP_ATTITUDE       = 108;   //out message         2 angles 1 heading
	private int MSP_ALTITUDE       = 109;   //out message         1 altitude
	private int MSP_BAT            = 110;   //out message         vbat, powermetersum
	private int MSP_RC_TUNING      = 111;   //out message         rc rate, rc expo, rollpitch rate, yaw rate, dyn throttle PID
	private int MSP_PID            = 112;   //out message         up to 16 P I D (8 are used)
	private int MSP_BOX            = 113;   //out message         up to 16 checkbox (11 are used)
	private int MSP_MISC           = 114;   //out message         powermeter trig + 8 free for future use
	private int MSP_MOTOR_PINS     = 115;   //out message         which pins are in use for motors & servos, for GUI 
	private int MSP_BOXNAMES       = 116;   //out message         the aux switch names
	private int MSP_PIDNAMES       = 117;   //out message         the PID names
	private int MSP_WP             = 118;   //out message         get a WP, WP# is in the payload, returns (WP#, lat, lon, alt, flags) WP#0-home, WP#16-poshold
	private int MSP_DEBUG          = 254;   //out message         debug1,debug2,debug3,debug4

	private int MSP_SET_RAW_GPS    = 201;   //in message          fix, numsat, lat, lon, alt, speed
	private int MSP_SET_PID        = 202;   //in message          up to 16 P I D (8 are used)
	private int MSP_SET_BOX        = 203;   //in message          up to 16 checkbox (11 are used)
	private int MSP_SET_RC_TUNING  = 204;   //in message          rc rate, rc expo, rollpitch rate, yaw rate, dyn throttle PID
	private int MSP_ACC_CALIBRATION= 205;   //in message          no param
	private int MSP_MAG_CALIBRATION= 206;   //in message          no param
	private int MSP_SET_MISC       = 207;   //in message          powermeter trig + 8 free for future use
	private int MSP_RESET_CONF     = 208;   //in message          no param
	private int MSP_WP_SET         = 209;   //in message          sets a given WP (WP#,lat, lon, alt, flags)
	private int MSP_EEPROM_WRITE   = 250;   //in message          no param
*/

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

	public String sendData(){
		String command = "$M<";
		
		return command;
			
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

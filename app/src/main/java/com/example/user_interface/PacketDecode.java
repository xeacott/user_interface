//Header Definitions
//bit:         Tag:      
//0			HR - Heart Rate
//1			EKG - Mv
//2			POA - IR			
//3			POB - R
//4			POx - Oxygenation
// expandable

//data containers, consider revision to single variable
public ArrayList<float> HR;
public ArrayList<float> EKG;
public ArrayList<float> POA;
public ArrayList<float> POB;
public ArrayList<float> POx;

public const int MaxMemLenght = 2500;

enum DataType{
	HR, EKG, POA, POB, POx;	
}

//library of tags
public const String[] StartTagDef = { "<hr>", "<ekg>", "<POA>", "<POB>", "<POx>"};
public const String[] EndTagDef = { "<\\hr>", "<\\ekg>", "<\\POA>", "<\\POB>", "<\\POx>"};

public void DataDecode(String msg){
	Char Header = msg.substring(3,4);
	int SensorDataStatus[8];
	//Bit decompose (translate character into string of bits)
	for (i = 0; i < 8; ++i) {
	  SensorDataStatus[i] = (Header >> i) & 1;
}

		String temp;
		for (i = 0; i < 5; i++) {
		  if(SensorDataStatus[i]) {
				//split off characters between start and end tags for datatype present in header
				temp = msg.split(StartTagDef[i])[1].split(EndTagDef[i])[0];

				//translate string snip to float
				float dataValue = Float.parseFloat(temp);

				//Update containers
				StorageUpdate( i, dataValue);
		  }
		}
}

public void StorageUpdate( int type, float DataValue){
	switch(type):
		case HR:
			if(HR.size() > MaxMemLenght){
				HR.remove(0);
			}
			HR.add(DataValue);
			break;
		case EKG:
			if(EKG.size() > MaxMemLenght){
				EKG.remove(0);
			}
			EKG.add(DataValue);
			break;
		case POA:
			if(POA.size() > MaxMemLenght){
				POA.remove(0);
			}
			POA.add(DataValue);
			break;
		case POB:
			if(POB.size() > MaxMemLenght){
				POB.remove(0);
			}
			POB.add(DataValue);
			break;
		case POx:
			if(POx.size() > MaxMemLenght){
				POx.remove(0);
			}
			POx.add(DataValue);
			break;
}
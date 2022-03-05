package utilities;


public class vcNode {

	private String svNest = null;
	private char c;

	public vcNode(String _vNest, char _c){
		c = _c;
		svNest = _vNest;
	}

	public String getStructure(){
		return svNest;
	}

	public void addFactor (String sStructure, char c){
		StringBuilder sb = new StringBuilder(sStructure + " x ");
		sb.append(svNest);
		svNest = sb.toString();
	}

	public Boolean contains(char _c){
		return svNest.indexOf(_c) >= 0;
	}

	public vcNode clone() {
		vcNode vcClone = new vcNode(svNest, c);
		return vcClone;
	}
}

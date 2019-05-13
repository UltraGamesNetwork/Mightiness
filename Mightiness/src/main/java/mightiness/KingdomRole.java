package mightiness;

enum KingdomRole {
	King("King"),
	Civilian("Civilian");
	
	public String Prefix;
	
	KingdomRole(String prefix){
		Prefix = prefix;
	}
}

package fca_sample;

public enum Criteria {
	CLASS_NAME_IN_KEYBOARD ("Class Name in Keyboard"),
	CLASS_NAME_IN_PARAMETER ("Class Name in Parameter"),
	CLASSES_ONLY ("Classes Only"),
	HIERARCHY_METHOD ("Hierarchy Method"),
	CROSSCUTTING_METHOD ("Crosscutting Method"),
	NONE ("None");
	
	private String name;
	
	Criteria(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}

package tagcloud;

public enum EnumCuboidOperations {
	PR("Project"), 
	RU("Roll-Up"),DD("Drill-Down"), SL("Slice"), DI("Dice"),
	SO("Sort"), ST("Strip Tags"), TN("Top N"), IC("Iceberg");
	protected String label;
	
	EnumCuboidOperations(String label){
		this.label= label;
	}
	
	public String getLabel() {
	      return this.label;
	}

}

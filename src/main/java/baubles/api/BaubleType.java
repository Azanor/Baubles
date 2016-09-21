package baubles.api;

public enum BaubleType {
	AMULET(0),
	RING(1,2),
	BELT(3);
	
	int[] validSlots;

	private BaubleType(int ... validSlots) {
		this.validSlots = validSlots;
	}
	
	public boolean hasSlot(int slot) {
		for (int s:validSlots) {
			if (s == slot) return true;
		}
		return false; 
	}
}

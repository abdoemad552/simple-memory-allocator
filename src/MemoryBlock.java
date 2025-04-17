public class MemoryBlock {
    
    private int size;
    private int startAddress;
    
    public MemoryBlock(int size) {
        this(size, -1);
    }
    
    public MemoryBlock(int size, int startAddress) {
        this.size = size;
        this.startAddress = startAddress;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public int getStartAddress() {
        return startAddress;
    }
    
    public void setStartAddress(int startAddress) {
        this.startAddress = startAddress;
    }
    
    public int getEndAddress() {
        return startAddress + size;
    }
    
    @Override
    public String toString() {
        return "MemoryBlock{" +
            "size=" + size +
            ", startAddress=" + startAddress +
            '}';
    }
}

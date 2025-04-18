
public class AllocatedBlock {
    
    private String name;
    
    private MemoryBlock block;
    
    public AllocatedBlock(String name, MemoryBlock block) {
        this.name = name;
        this.block = block;
    }
    
    public AllocatedBlock(String name, int size, int startAddress) {
        this.name = name;
        this.block = new MemoryBlock(size, startAddress);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public MemoryBlock getBlock() {
        return block;
    }
    
    public void setBlock(MemoryBlock block) {
        this.block = block;
    }
    
    public int getSize() {
        return block.getSize();
    }
    
    public void setSize(int size) {
        this.block.setSize(size);
    }
    
    public int getStartAddress() {
        return block.getStartAddress();
    }
    
    public void setStartAddress(int startAddress) {
        this.block.setStartAddress(startAddress);
    }
    
    public int getEndAddress() {
        return block.getEndAddress();
    }
}

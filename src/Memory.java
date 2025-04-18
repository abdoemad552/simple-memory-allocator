
import java.util.*;

public class Memory {
    
    private final int size;
    
    private final ArrayList<MemoryBlock> freeList;
    private final ArrayList<AllocatedBlock> busyList;
    
    public Memory(int size) {
        this.size = size;
        this.freeList = new ArrayList<>();
        this.busyList = new ArrayList<>();
        
        freeList.add(new MemoryBlock(size, 0));
    }
    
    public void request(String name, int size, char strategy) {
        int blockIndex = findBlock(size, strategy);
        
        if (blockIndex == -1) {
            throw new RuntimeException("Error: no available space for this process");
        }
        
        // Check if the block will have remaining free space.
        // If so, then create a new free memory block.
        if (freeList.get(blockIndex).getSize() != size) {
            freeList.add(blockIndex + 1, new MemoryBlock(
                freeList.get(blockIndex).getSize() - size,
                freeList.get(blockIndex).getStartAddress() + size
            ));
        }
        
        
        AllocatedBlock allocatedBlock =
            new AllocatedBlock(name, freeList.remove(blockIndex));
        allocatedBlock.setSize(size);
        
        if (busyList.isEmpty()) {
            busyList.add(allocatedBlock);
        } else {
            blockIndex = -1;
            for (int i = 0; i < busyList.size(); i++) {
                if (busyList.get(i).getStartAddress() > allocatedBlock.getStartAddress()) {
                    blockIndex = i;
                    busyList.add(blockIndex, allocatedBlock);
                    break;
                }
            }
            
            if (blockIndex == -1) {
                busyList.add(allocatedBlock);
            }
        }
    }
    
    public void release(String name) {
        int blockIndex = findByName(name);
        
        if (blockIndex != -1) {
            AllocatedBlock allocatedBlock = busyList.remove(blockIndex);
            System.out.printf("%s\n", allocatedBlock);
            
            if (freeList.isEmpty()) {
                freeList.add(allocatedBlock.getBlock());
            } else {
                blockIndex = -1;
                for (int i = 0; i < freeList.size(); i++) {
                    if (freeList.get(i).getStartAddress() > allocatedBlock.getStartAddress()) {
                        blockIndex = i;
                        freeList.add(blockIndex, allocatedBlock.getBlock());
                        break;
                    }
                }
                
                if (blockIndex == -1) {
                    blockIndex = freeList.size();
                    freeList.add(allocatedBlock.getBlock());
                }
                
                allocatedBlock.setBlock(null);
                
                if (blockIndex > 0) {
                    if (freeList.get(blockIndex - 1).getEndAddress() == freeList.get(blockIndex).getStartAddress()) {
                        MemoryBlock prev = freeList.remove(--blockIndex);
                        freeList.get(blockIndex).setStartAddress(prev.getStartAddress());
                        freeList.get(blockIndex).setSize(prev.getSize() + freeList.get(blockIndex).getSize());
                    }
                }
                
                if (blockIndex < freeList.size() - 1) {
                    if (freeList.get(blockIndex).getEndAddress() == freeList.get(blockIndex + 1).getStartAddress()) {
                        MemoryBlock next = freeList.remove(blockIndex + 1);
                        freeList.get(blockIndex).setSize(next.getSize() + freeList.get(blockIndex).getSize());
                    }
                }
            }
        }
    }
    
    private int findByName(String name) {
        for (int i = 0; i < busyList.size(); i++) {
            if (busyList.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }
    
    public int findBlock(int size, char strategy) {
        int index = -1;
        
        switch (strategy) {
            case 'f':
                for (int i = 0; i < freeList.size(); i++) {
                    if (freeList.get(i).getSize() >= size) {
                        index = i;
                        break;
                    }
                }
                break;
            case 'b':
                for (int i = 0; i < freeList.size(); i++) {
                    if (
                        freeList.get(i).getSize() >= size &&
                        (index == -1 || freeList.get(i).getSize() < freeList.get(index).getSize())
                    ) {
                        index = i;
                    }
                }
                break;
            case 'w':
                for (int i = 0; i < freeList.size(); i++) {
                    if (
                        freeList.get(i).getSize() >= size &&
                        (index == -1 || freeList.get(i).getSize() > freeList.get(index).getSize())
                    ) {
                        index = i;
                    }
                }
                break;
        }
        
        return index;
    }
    
    public void compact() {
        if (busyList.isEmpty() || freeList.isEmpty()) return;
        
        busyList.get(0).setStartAddress(0);
        
        for (int i = 1; i < busyList.size(); i++) {
            busyList.get(i).setStartAddress(busyList.get(i - 1).getEndAddress());
        }
        
        freeList.clear();
        
        freeList.add(new MemoryBlock(
            this.size - busyList.get(busyList.size() - 1).getEndAddress(),
            busyList.get(busyList.size() - 1).getEndAddress()
        ));
    }
    
    public void status() {
        System.out.printf("+------------------------------------------+\n");
        System.out.printf("|                 Free List                |\n");
        System.out.printf("+---------------+------------+-------------+\n");
        System.out.printf("| Start Address | Block Size | End Address |\n");
        System.out.printf("+---------------+------------+-------------+\n");
        if (freeList.isEmpty()) {
            System.out.printf("|                   Empty                  |\n");
            System.out.printf("+------------------------------------------+\n");
        } else {
            for (MemoryBlock block : freeList) {
                System.out.printf(
                    "| %13d | %10d | %11d |\n",
                    block.getStartAddress(),
                    block.getSize(),
                    block.getEndAddress()
                );
                System.out.printf("+---------------+------------+-------------+\n");
            }
        }
        
        System.out.printf("+---------------------------------------------------------+\n");
        System.out.printf("|                       Busy List                         |\n");
        System.out.printf("+--------------+---------------+------------+-------------+\n");
        System.out.printf("| Process Name | Start Address | Block Size | End Address |\n");
        System.out.printf("+--------------+---------------+------------+-------------+\n");
        if (busyList.isEmpty()) {
            System.out.printf("|                        Empty                            |\n");
            System.out.printf("+---------------------------------------------------------+\n");
        } else {
            for (AllocatedBlock allocatedBlock : busyList) {
                System.out.printf(
                    "| %-12s | %13d | %10d | %11d |\n",
                    allocatedBlock.getName(),
                    allocatedBlock.getStartAddress(),
                    allocatedBlock.getSize(),
                    allocatedBlock.getEndAddress()
                );
                System.out.printf("+--------------+---------------+------------+-------------+\n");
            }
        }
    }
    
    public int getSize() {
        return size;
    }
}

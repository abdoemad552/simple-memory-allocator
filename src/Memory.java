import java.util.*;

public class Memory {
    
    private final int size;
    
    private final ArrayList<MemoryBlock> freeList;
    private final ArrayList<MemoryBlock> busyList;
    
    private final Map<String, Integer> allocationIndex;
    
    public Memory(int size) {
        this.size = size;
        this.freeList = new ArrayList<>();
        this.busyList = new ArrayList<>();
        this.allocationIndex = new HashMap<>();
        
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
        
        MemoryBlock block = freeList.remove(blockIndex);
        block.setSize(size);
        
        int index = -1;
        if (busyList.isEmpty()) {
            busyList.add(block);
            index = 0;
        } else {
            for (int i = 0; i < busyList.size(); i++) {
                if (busyList.get(i).getStartAddress() > block.getStartAddress()) {
                    index = i;
                    busyList.add(index, block);
                    break;
                }
            }
            
            if (index == -1) {
                index = busyList.size();
                busyList.add(block);
            }
        }
        
        allocationIndex.put(name, index);
    }
    
    public void release(String name) {
        if (allocationIndex.containsKey(name)) {
            int blockIndex = allocationIndex.get(name);
            
            MemoryBlock block = busyList.remove(blockIndex);
            System.out.printf("%s\n", block);
            
            if (freeList.isEmpty()) {
                freeList.add(block);
            } else {
                blockIndex = -1;
                for (int i = 0; i < freeList.size(); i++) {
                    if (freeList.get(i).getStartAddress() > block.getStartAddress()) {
                        blockIndex = i;
                        freeList.add(blockIndex, block);
                        break;
                    }
                }
                
                if (blockIndex == -1) {
                    blockIndex = freeList.size();
                    freeList.add(block);
                }
                
                if (blockIndex > 0) {
                    if (freeList.get(blockIndex - 1).getEndAddress() == block.getStartAddress()) {
                        MemoryBlock prev = freeList.remove(blockIndex - 1);
                        block.setStartAddress(prev.getStartAddress());
                        block.setSize(prev.getSize() + block.getSize());
                        blockIndex--;
                    }
                }
                
                if (blockIndex < freeList.size() - 1) {
                    if (freeList.get(blockIndex + 1).getStartAddress() == block.getEndAddress()) {
                        MemoryBlock next = freeList.remove(blockIndex + 1);
                        block.setSize(next.getSize() + block.getSize());
                    }
                }
            }
        }
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
        
        System.out.printf("+----------------------------------------------------------+\n");
        System.out.printf("|                        Busy List                         |\n");
        System.out.printf("+---------------+---------------+------------+-------------+\n");
        System.out.printf("| Process Index | Start Address | Block Size | End Address |\n");
        System.out.printf("+---------------+---------------+------------+-------------+\n");
        if (busyList.isEmpty()) {
            System.out.printf("|                         Empty                            |\n");
            System.out.printf("+----------------------------------------------------------+\n");
        } else {
            for (int i = 0; i < busyList.size(); i++) {
                System.out.printf(
                    "| %13d | %13d | %10d | %11d |\n",
                    i,
                    busyList.get(i).getStartAddress(),
                    busyList.get(i).getSize(),
                    busyList.get(i).getEndAddress()
                );
                System.out.printf("+---------------+---------------+------------+-------------+\n");
            }
        }
    }
    
    public int getSize() {
        return size;
    }
}

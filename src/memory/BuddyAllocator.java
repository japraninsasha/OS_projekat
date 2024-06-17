package memory;

import java.util.*;

public class BuddyAllocator {
    private final int minBlockSize;
    private final int maxBlockSize;
    private final int totalSize;
    private final TreeMap<Integer, LinkedList<Integer>> freeLists;
    private final int[] memory; // Simulacija memorijskog prostora

    public BuddyAllocator(int minBlockSize, int maxBlockSize, int totalSize) {
        this.minBlockSize = minBlockSize;
        this.maxBlockSize = maxBlockSize;
        this.totalSize = totalSize;
        this.memory = new int[totalSize]; // Inicijalizacija memorijskog prostora
        this.freeLists = new TreeMap<>();
        initializeFreeLists();
    }

    private void initializeFreeLists() {
        int size = minBlockSize;
        while (size <= maxBlockSize) {
            freeLists.put(size, new LinkedList<>());
            size *= 2;
        }
        freeLists.get(maxBlockSize).add(0);
    }

    public int allocate(int size) {
        int allocSize = Math.max(minBlockSize, Integer.highestOneBit(size));
        while (!freeLists.containsKey(allocSize) || freeLists.get(allocSize).isEmpty()) {
            allocSize *= 2;
            if (allocSize > maxBlockSize) {
                return -1;
            }
        }

        int addr = freeLists.get(allocSize).removeFirst();
        while (allocSize > size) {
            allocSize /= 2;
            freeLists.get(allocSize).add(addr + allocSize);
        }
        return addr;
    }

    public void deallocate(int addr, int size) {
        while (size <= maxBlockSize) {
            LinkedList<Integer> list = freeLists.get(size);
            Integer buddyAddr = addr ^ size;
            if (!list.remove(buddyAddr)) {
                list.add(addr);
                break;
            }
            addr = Math.min(addr, buddyAddr);
            size *= 2;
        }
    }

    public int[] getMemory(int startAddress, int size) {
        int[] data = new int[size];
        System.arraycopy(memory, startAddress, data, 0, size);
        return data;
    }

    public void printMemory() {
        System.out.println("Memory Allocation Status:");
        for (Map.Entry<Integer, LinkedList<Integer>> entry : freeLists.entrySet()) {
            System.out.println("Block size: " + entry.getKey() + ", Free blocks: " + entry.getValue());
        }
    }
}
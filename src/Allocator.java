import java.util.*;

public class Allocator {
    private static final Scanner input;
    
    static {
        input = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
		if (args.length < 1) {
            System.out.printf("Error: ./allocator <size>\n");
            System.exit(1);
        }

        long size = -1;
        try {
             size = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            System.out.printf("Error: capacity should be a valid integer\n");
            System.exit(1);
        }

        if (size > Integer.MAX_VALUE) {
            System.out.printf(
                "Error: capacity must be in [%d, %d]\n", 1, Integer.MAX_VALUE);
            System.exit(1);
        }
        
        Memory memory = new Memory((int) size);
        
		System.out.printf("Memory allocator is ready ;)\n");

		while (true) {
            System.out.printf("allocator> ");
            
            String command = input.nextLine().trim();
            
            if (command.isEmpty()) {
                continue;
            }
            
            String[] tokens = command.split(" ");
            
            switch (tokens[0].toLowerCase()) {
                case "rq": {
                    if (tokens.length != 4) {
                        System.out.printf("Error: Invalid RQ command\n");
                        System.out.printf("Syntax: RQ <name> <size> <strategy>\n");
                        continue;
                    }
                    
                    String name = tokens[1];
                    
                    int allocationSize;
                    try {
                        allocationSize = Integer.parseInt(tokens[2]);
                    } catch (NumberFormatException e) {
                        System.out.printf("Error: allocation size should be a valid integer\n");
                        continue;
                    }
                    
                    char strategy = tokens[3].toLowerCase().charAt(0);
                    
                    if (strategy != 'f' && strategy != 'b' && strategy != 'w') {
                        System.out.printf("Error: invalid allocation strategy\n");
                        System.out.printf("-> F (First fit)\n");
                        System.out.printf("-> B (Best fit)\n");
                        System.out.printf("-> W (Worst fit)\n");
                        continue;
                    }
                    
                    try {
                        memory.request(name, allocationSize, strategy);
                    } catch (RuntimeException e) {
                        System.out.println(e.getMessage());
                        continue;
                    }
                    
                    System.out.printf("Allocated.\n");
                    break;
                } case "rl": {
                    if (tokens.length != 2) {
                        System.out.printf("Error: Invalid RL command\n");
                        System.out.printf("Syntax: RL <name>>\n");
                        continue;
                    }
                    
                    String name = tokens[1];
                    
                    memory.release(name);
                    break;
                } case "c": {
                    memory.compact();
                    break;
                } case "stat": {
                    memory.status();
                    break;
                } case "exit": case "q": {
					System.out.printf("Bye\n");
					System.exit(0);
                } default: {
                    System.out.printf("Invalid command\n");
                    showAvailableCommands();
                }
            }
        }
    }
    
    private static void showAvailableCommands() {
		System.out.printf("Available commands:\n");
    	System.out.printf("Request allocation:    > RQ <name> <size> <strategy>\n");
        System.out.printf("Release a process:     > RL <name>\n");
        System.out.printf("Perform compaction:    > C\n");
        System.out.printf("Status of the memory:  > STAT\n");
        System.out.printf("Exit the program:      > EXIT or > Q\n");
    }
}

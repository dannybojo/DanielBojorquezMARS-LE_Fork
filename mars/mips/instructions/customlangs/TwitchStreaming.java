    package mars.mips.instructions.customlangs;
    import mars.simulator.*;
    import mars.mips.hardware.*;
    import mars.mips.instructions.syscalls.*;
    import mars.*;
    import mars.util.*;
    import java.util.*;
    import java.io.*;
    import mars.mips.instructions.*;
    import java.util.Random;

public class TwitchStreaming extends CustomAssembly{

    @Override
    public String getName(){
        return "Twitch Streaming";
    }

    @Override
    public String getDescription(){
        return "Simulate a Twitch Streaming environment!";
    }

    @Override
    protected void populate(){

        // start stream (simpler version of addi)
        instructionList.add(
            new BasicInstruction("ss $t0, 100",
                "Start Stream: Stores a positive immediate value into $t0.",
                BasicInstructionFormat.I_FORMAT,
                "000001 fffff 00000 ssssssssssssssss",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == -1)
                        {
                            SystemIO.printString("This streamer is banned!\n");
                            return;
                        }
                        String name = getRegisterName(operands[0]);
                        int value = Math.abs(operands[1]) << 16 >> 16;
                        RegisterFile.updateRegister(operands[0], value);
                        SystemIO.printString(name + "'s stream started with " + value + " viewers.\n");
                    }
                }));

        // buy viewbots (same as addi)
        instructionList.add(
            new BasicInstruction("bvb $t0, $t1, 15",
                "Buy Viewbots: Stores the sum of $t1 and a positive immediate value into $t0.",
                BasicInstructionFormat.I_FORMAT,
                "000001 sssss fffff tttttttttttttttt",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == -1
                            || RegisterFile.getValue(operands[1]) == -1)
                        {
                            SystemIO.printString("Interacting with banned channels is against TOS: get BANNED!\n");
                            RegisterFile.updateRegister(operands[0], -1);
                            RegisterFile.updateRegister(operands[1], -1);
                            return;
                        }
                        String name = getRegisterName(operands[0]);
                        int placeholder = RegisterFile.getValue(operands[1]);
                        int value = Math.abs(operands[2]) << 16 >> 16;
                        int result = placeholder + value;
                        if ((placeholder >= 0 && value >= 0 && result < 0) ||
                                (placeholder < 0 && value < 0 && result >= 0))
                        {
                            throw new ProcessingException(statement,
                                    "arithmetic overflow", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                        }
                        RegisterFile.updateRegister(operands[0], result);
                        SystemIO.printString(name + " purchased " + result + " viewbots.\n");
                    }
                }));

        // collab (same as add)
        instructionList.add(
            new BasicInstruction("col $t0, $t1, $t2",
                "Collab: Stores the sum of $t1 and $t2 into $t0.",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 00000",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == -1
                                || RegisterFile.getValue(operands[1]) == -1
                                || RegisterFile.getValue(operands[2]) == -1)
                        {
                            SystemIO.printString("Interacting with banned channels is against TOS: get BANNED!\n");
                            RegisterFile.updateRegister(operands[0], -1);
                            RegisterFile.updateRegister(operands[1], -1);
                            RegisterFile.updateRegister(operands[2], -1);
                            return;
                        }
                        String name0 = getRegisterName(operands[0]);
                        String name1 = getRegisterName(operands[1]);
                        String name2 = getRegisterName(operands[2]);
                        int value1 = RegisterFile.getValue(operands[1]);
                        int value2 = RegisterFile.getValue(operands[2]);
                        int sum = value1 + value2;
                        // overflow on A+B detected when A and B have same sign and A+B has other sign.
                        if ((value1 >= 0 && value2 >= 0 && sum < 0)
                                || (value1 < 0 && value2 < 0 && sum >= 0))
                        {
                            throw new ProcessingException(statement,
                                    "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                        }
                        RegisterFile.updateRegister(operands[0], sum);
                        SystemIO.printString(name1 + " and " + name2 + " collabed on " + name0 + "'s channel.\n");
                    }
                }));

        // poach
        instructionList.add(
            new BasicInstruction("p $t0, $t1, $t2",
                "Poach: Stores the difference of $t1 and $t2 into $t0.",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 00001",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == -1
                                || RegisterFile.getValue(operands[1]) == -1
                                || RegisterFile.getValue(operands[2]) == -1)
                        {
                            SystemIO.printString("Interacting with banned channels is against TOS: get BANNED!\n");
                            RegisterFile.updateRegister(operands[0], -1);
                            RegisterFile.updateRegister(operands[1], -1);
                            RegisterFile.updateRegister(operands[2], -1);
                            return;
                        }
                        String name0 = getRegisterName(operands[0]);
                        String name1 = getRegisterName(operands[1]);
                        String name2 = getRegisterName(operands[2]);
                        int value1 = RegisterFile.getValue(operands[1]);
                        int value2 = RegisterFile.getValue(operands[2]);
                        int dif = value1 - value2;
                        if ((value1 >= 0 && value2 < 0 && dif < 0)
                                || (value1< 0 && value2 >= 0 && dif >= 0))
                        {
                            throw new ProcessingException(statement,
                                    "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                        }
                        RegisterFile.updateRegister(operands[0], dif);
                        SystemIO.printString(name0 + " and " + name1 + " poached viewers from " + name2 + ".\n");
                    }
                }));

        // clip farm
        instructionList.add(
            new BasicInstruction("cf $t0, $t1, 100",
                "Clip Farm: Multiplies $t1 and a positive immediate value. Set hi to high-order 32 bits, $t0 to low order 32 bits.",
                BasicInstructionFormat.I_FORMAT,
                "000011 fffff sssss tttttttttttttttt",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == -1
                                || RegisterFile.getValue(operands[1]) == -1)
                        {
                            SystemIO.printString("Interacting with banned channels is against TOS: get BANNED!\n");
                            RegisterFile.updateRegister(operands[0], -1);
                            RegisterFile.updateRegister(operands[1], -1);
                            return;
                        }
                        String name = getRegisterName(operands[0]);
                        long immediate = Math.abs(operands[2]);
                        long product = (long) RegisterFile.getValue(operands[1])
                                * immediate;
                        RegisterFile.updateRegister(33, (int) (product >> 32));
                        RegisterFile.updateRegister(operands[0], (int) ((product << 32) >> 32));
                        SystemIO.printString(name + " clip farmed and gained " + product + " viewers.\n");
                    }
                }));

        // fall off
        instructionList.add(
            new BasicInstruction("fo $t0, $t1, 5",
                "Fall Off: Divides ($t1 / immediate value). Set $t0 to quotient.",
                BasicInstructionFormat.I_FORMAT,
                "000100 fffff sssss tttttttttttttttt",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == -1
                                || RegisterFile.getValue(operands[1]) == -1)
                        {
                            SystemIO.printString("Interacting with banned channels is against TOS: get BANNED!\n");
                            RegisterFile.updateRegister(operands[0], -1);
                            RegisterFile.updateRegister(operands[1], -1);
                            return;
                        }
                        String name = getRegisterName(operands[0]);
                        int immediate = operands[2];
                        if (immediate == 0)
                            return;

                        RegisterFile.updateRegister(operands[0],
                                RegisterFile.getValue(operands[1]) / immediate);
                        SystemIO.printString(name + " fell off...\n");
                    }
                }));

        // niche community
        instructionList.add(
            new BasicInstruction("nic $t0, $t1, 5",
                "Niche Community: Divides ($t1 / (+)immediate value). Stores the remainder in $t0.",
                BasicInstructionFormat.I_FORMAT,
                "000101 fffff sssss tttttttttttttttt",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == -1
                                || RegisterFile.getValue(operands[1]) == -1)
                        {
                            SystemIO.printString("Interacting with banned channels is against TOS: get BANNED!\n");
                            RegisterFile.updateRegister(operands[0], -1);
                            RegisterFile.updateRegister(operands[1], -1);
                            return;
                        }
                        String name = getRegisterName(operands[0]);
                        int immediate = Math.abs(operands[2]);
                        if (immediate == 0)
                            return;

                        RegisterFile.updateRegister((operands[0]),
                                RegisterFile.getValue(operands[1]) % immediate);
                        SystemIO.printString("Hello " + name + " niche community <3\n");
                    }
                }));

        // mog
        instructionList.add(
            new BasicInstruction("mog $t0, $t1, label",
                "Mog: Branches to address stored at label if $t0 > $t1.",
                BasicInstructionFormat.I_FORMAT,
                "000110 fffff sssss tttttttttttttttt",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == -1
                                || RegisterFile.getValue(operands[1]) == -1)
                        {
                            SystemIO.printString("Interacting with banned channels is against TOS: get BANNED!\n");
                            RegisterFile.updateRegister(operands[0], -1);
                            RegisterFile.updateRegister(operands[1], -1);
                            return;
                        }
                        String name0 = getRegisterName(operands[0]);
                        String name1 = getRegisterName(operands[1]);
                        if (RegisterFile.getValue(operands[0]) > RegisterFile.getValue(operands[1]))
                        {
                            Globals.instructionSet.processBranch(operands[2]);
                        }
                        SystemIO.printString(name0 + " mogged " + name1 + ".\n");
                    }
                }));

        // rival
        instructionList.add(
            new BasicInstruction("riv $t0, $t1, label",
                "Rival: Jumps to address stores at label if $t0 == $t1.",
                BasicInstructionFormat.I_FORMAT,
                "000011 fffff sssss tttttttttttttttt",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == -1
                                || RegisterFile.getValue(operands[1]) == -1)
                        {
                            SystemIO.printString("Interacting with banned channels is against TOS: get BANNED!\n");
                            RegisterFile.updateRegister(operands[0], -1);
                            RegisterFile.updateRegister(operands[1], -1);
                            return;
                        }
                        if (RegisterFile.getValue(operands[0]) == RegisterFile.getValue(operands[1]))
                        {
                            Globals.instructionSet.processBranch(operands[2]);
                        }
                        String name0 = getRegisterName(operands[0]);
                        String name1 = getRegisterName(operands[1]);
                        SystemIO.printString(name0 + " and " + name1 + " are rivals!\n");
                    }
                }));

        // trend hop
        instructionList.add(
            new BasicInstruction("hop label",
                "Trend Hop: Jumps to statement at the address stored at the label.",
                BasicInstructionFormat.J_FORMAT,
                "001000 ffffffffffffffffffffffffff",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        Globals.instructionSet.processJump(
                                ((RegisterFile.getProgramCounter() & 0xF0000000)
                                | (operands[0] << 2)));
                    }
                }));

        // irl stream
        instructionList.add(
            new BasicInstruction("irl $t0",
                "\"IRL\" Stream: Random chance for $t0 to double, half, +50, or = 0.",
                BasicInstructionFormat.R_FORMAT,
                "001001 00000 00000 fffff 00000 000000",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == -1)
                        {
                            SystemIO.printString("This streamer is banned!\n");
                            RegisterFile.updateRegister(operands[0], -1);
                            return;
                        }
                        Random random = new Random();
                        int value = RegisterFile.getValue(operands[0]);
                        int num = random.nextInt(4);
                        String name = getRegisterName(operands[0]);
                        switch (num) {
                            case 0:
                                RegisterFile.updateRegister(operands[0], value * 2);
                                SystemIO.printString(name + " got assaulted! Viewers doubled!\n");
                                break;
                            case 1:
                                RegisterFile.updateRegister(operands[0], value / 2);
                                SystemIO.printString(name + "'s stream is boring... Viewers halved!\n");
                                break;
                            case 2:
                                RegisterFile.updateRegister(operands[0], value + 50);
                                SystemIO.printString(name + " was a good samaritan! Plus 50 viewers!\n");
                                break;
                            case 3:
                                RegisterFile.updateRegister(operands[0], 0);
                                SystemIO.printString(name + "'s camera broke! All the viewers left :/\n");
                                break;
                        }
                    }
                }));

        // stream raid
        instructionList.add(
            new BasicInstruction("raid $t0, $t1",
                "Stream Raid: $t1 raids $t0, adding $t0 to $t1.",
                BasicInstructionFormat.R_FORMAT,
                "001001 sssss 00000 fffff 00000 000001",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == -1
                                || RegisterFile.getValue(operands[1]) == -1)
                        {
                            SystemIO.printString("Interacting with banned channels is against TOS: get BANNED!\n");
                            RegisterFile.updateRegister(operands[0], -1);
                            RegisterFile.updateRegister(operands[1], -1);
                            return;
                        }
                        String name0 = getRegisterName(operands[0]);
                        String name1 = getRegisterName(operands[1]);
                        RegisterFile.updateRegister(operands[1],
                                RegisterFile.getValue(operands[0] + RegisterFile.getValue(operands[1])));
                        SystemIO.printString(name1 + " raided " + name0 + "'s stream!\n");
                    }
                }));

        // ban
        instructionList.add(
            new BasicInstruction("ban $t0",
                "Ban: Sets $t0 to -1 and distributes value evenly among other number registers." +
                        " If any other register interacts with $t0, they also get banned.",
                BasicInstructionFormat.R_FORMAT,
                "001001 00000 00000 fffff 00000 000010",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int value = RegisterFile.getValue(operands[0]) / 9;
                        // distribute viewers evenly among other registers
                        for (int i = 8; i < 16; i++){
                            if (RegisterFile.getValue(i) != -1 && i != operands[0])
                                RegisterFile.updateRegister(i, RegisterFile.getValue(i) + value);
                        }
                        if (RegisterFile.getValue(24) != -1 && 24 != operands[0])
                            RegisterFile.updateRegister(24, RegisterFile.getValue(24) + value);
                        if (RegisterFile.getValue(25) != -1 && 25 != operands[0])
                            RegisterFile.updateRegister(25, RegisterFile.getValue(25) + value);

                        RegisterFile.updateRegister(operands[0], -1);
                        String name = getRegisterName(operands[0]);
                        SystemIO.printString(name + " has been banned.\n");
                    }
                }));

        // server outage
        instructionList.add(
            new BasicInstruction("serverdown",
                "Server Outage: Sets $t0-$t9 to 0 if they're not banned.",
                BasicInstructionFormat.R_FORMAT,
                "001001 00000 00000 00000 00000 000011",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        // only set unbanned channels to 0
                        for (int i = 8; i < 16; i++){
                            if (RegisterFile.getValue(i) != -1)
                                RegisterFile.updateRegister(i, 0);
                        }
                        if (RegisterFile.getValue(24) != -1)
                            RegisterFile.updateRegister(24, 0);
                        if (RegisterFile.getValue(25) != -1)
                            RegisterFile.updateRegister(25, 0);
                        SystemIO.printString("Server's went down, all streams ended.\n");
                    }
                }));

        // swat
        instructionList.add(
            new BasicInstruction("swat $t0",
                "SWAT: If value stored in $t0 is greater than 1000, then random chance to either" +
                        " get swatted and lose all viewers or for nothing to happen.",
                BasicInstructionFormat.R_FORMAT,
                "001001 00000 00000 fffff 00000 000100",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int value = RegisterFile.getValue(operands[0]);
                        Random random = new Random();
                        int num = random.nextInt();
                        String name = getRegisterName(operands[0]);
                        if (value > 1000)
                        {
                            if (num%2 == 0)
                            {
                                RegisterFile.updateRegister(operands[0], 0);
                                SystemIO.printString(name + " got swatted and ended their stream...\n");
                            }
                            else
                            {
                                SystemIO.printString("Failed swatting attempt on " + name + ", chatter was banned.\n");
                            }
                        }
                        else
                            SystemIO.printString(name + "'s community too small, they wouldn't swat him!\n");
                    }
                }));

        // sub-a-thon
        instructionList.add(
            new BasicInstruction("sub $t0",
                "Sub-a-thon: 50% chance to add 100 to $t0. If successful, then 50% chance to add double that (200)." +
                        " If successful, 50% chance to add double that (400). Repeats until failure.",
                BasicInstructionFormat.R_FORMAT,
                "001001 00000 00000 fffff 00000 000101",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == -1)
                        {
                            SystemIO.printString("This streamer is banned!\n");
                            return;
                        }
                        Random random = new Random();
                        String name = getRegisterName(operands[0]);
                        int num = random.nextInt();
                        int counter = 0;
                        int value = 100;
                        if (num%2 != 0)
                        {
                            SystemIO.printString("Sub-a-thon was a total flop...\n");
                            return;
                        }
                        while (num % 2 == 0)
                        {
                            RegisterFile.updateRegister(operands[0],
                                    RegisterFile.getValue(operands[0]) + value);
                            value *= 2;
                            counter += 1;
                            num = random.nextInt();
                        }
                        SystemIO.printString(name + "'s sub-a-thon lasted " + counter + " days!\n");
                    }
                }));

        // check analytics
        instructionList.add(
            new BasicInstruction("can $t0",
                "Check Analytics: Prints current viewership, as well as a \"Subscriber\" count (10-50% of viewership).",
                BasicInstructionFormat.R_FORMAT,
                "001001 00000 00000 fffff 00000 000110",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int views = RegisterFile.getValue(operands[0]);
                        if (views == -1)
                        {
                            SystemIO.printString("This streamer is banned!\n");
                            return;
                        }
                        Random random = new Random();
                        double percent = random.nextInt(41) + 10;
                        percent = percent/100;
                        int subcount = (int) (views * percent);
                        String name = getRegisterName(operands[0]);
                        SystemIO.printString(name + "'s Analytics:\n" +
                                "   " + views + " viewers\n" +
                                "   " + subcount + " subscribers\n");
                    }
                }));

        // celebrity cameo
        instructionList.add(
            new BasicInstruction("cam $t0, $t1, $t2",
                "Celebrity Cameo: Applies multiplier of ($t1 + $t2) on $t0.",
                BasicInstructionFormat.R_FORMAT,
                "001001 sssss ttttt fffff 00000 000111",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == -1
                                || RegisterFile.getValue(operands[1]) == -1
                                || RegisterFile.getValue(operands[2]) == -1)
                        {
                            SystemIO.printString("Interacting with banned channels is against TOS: get BANNED!\n");
                            RegisterFile.updateRegister(operands[0], -1);
                            RegisterFile.updateRegister(operands[1], -1);
                            RegisterFile.updateRegister(operands[2], -1);
                            return;
                        }

                        int value0 = RegisterFile.getValue(operands[0]);
                        int value1 = RegisterFile.getValue(operands[1]);
                        int value2 = RegisterFile.getValue(operands[2]);
                        int multiplier = value1 + value2;
                        RegisterFile.updateRegister(operands[0], value0 * multiplier);

                        String name0 = getRegisterName(operands[0]);
                        String name1 = getRegisterName(operands[1]);
                        String name2 = getRegisterName(operands[2]);
                        SystemIO.printString(name0 + " had cameos from celebrities " + name1 + " and " + name2 + "!\n");
                    }
                }));

        // end stream
        instructionList.add(
            new BasicInstruction("es $t0, $t1",
                "End Stream: Adds $t0 to $t1 and sets $t0 to 0.",
                BasicInstructionFormat.R_FORMAT,
                "001001 fffff 00000 sssss 00000 001000",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == -1
                                || RegisterFile.getValue(operands[1]) == -1)
                        {
                            SystemIO.printString("Interacting with banned channels is against TOS: get BANNED!\n");
                            RegisterFile.updateRegister(operands[0], -1);
                            RegisterFile.updateRegister(operands[1], -1);
                            return;
                        }
                        int value0 = RegisterFile.getValue(operands[0]);
                        int value1 = RegisterFile.getValue(operands[1]);
                        RegisterFile.updateRegister(operands[1], value0 + value1);
                        RegisterFile.updateRegister(operands[0], 0);

                        String name = getRegisterName(operands[0]);
                        SystemIO.printString(name + " ended their stream.\n");
                    }
                }));

        // big donation
        instructionList.add(
            new BasicInstruction("bdon $t0, $t1, 500",
                "Big Donation: If $t0 has less than 1/5 of $t1's value, then add positive immediate value to $t0.",
                BasicInstructionFormat.I_FORMAT,
                "001010 fffff sssss tttttttttttttttt",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        if (RegisterFile.getValue(operands[0]) == -1
                                || RegisterFile.getValue(operands[1]) == -1)
                        {
                            SystemIO.printString("Interacting with banned channels is against TOS: get BANNED!\n");
                            RegisterFile.updateRegister(operands[0], -1);
                            RegisterFile.updateRegister(operands[1], -1);
                            return;
                        }
                        String name0 = getRegisterName(operands[0]);
                        String name1 = getRegisterName(operands[1]);
                        int num = RegisterFile.getValue(operands[1]) / 5;
                        if (RegisterFile.getValue(operands[0]) < num)
                        {
                            int value = RegisterFile.getValue(operands[0]);
                            int immediate = Math.abs(operands[2]) << 16 >> 16;
                            RegisterFile.updateRegister(operands[0], value + immediate);
                            SystemIO.printString(name1 + " gave a big donation to " + name0 + "!\n");
                        } else {
                            SystemIO.printString("No donation was made.\n");
                        }
                    }
                }));

        // chat spam
        instructionList.add(
            new BasicInstruction("spam variable",
                "Repeatedly prints the string stored at the address/variable 5-20 times.",
                BasicInstructionFormat.I_FORMAT,
                "001011 00000 00000 ffffffffffffffff",
                new SimulationCode()
                {
                    @Override
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();

                        RegisterFile.updateRegister(2, operands[0] << 16);
                        RegisterFile.updateRegister(2, RegisterFile.getValue(2) | (operands[0]));
                        Random random = new Random();
                        int spamNum = random.nextInt(16) + 5;
                        for (int i = 0; i < spamNum; i ++) {
                            int byteAddress = RegisterFile.getValue(2);
                            char ch = 0;
                            try {
                                ch = (char) Globals.memory.getByte(byteAddress);
                                while (ch != 0) {
                                    SystemIO.printString(new Character(ch).toString());
                                    byteAddress++;
                                    ch = (char) Globals.memory.getByte(byteAddress);
                                }
                            } catch (AddressErrorException e) {
                                throw new ProcessingException(statement, e);
                            }
                        }
                    }
                }));
    }

    // helper function to return string names of registers
    // used mostly for printing messages
    public String getRegisterName(int number){
        return RegisterFile.getRegisters()[number].getName();
    }
}
# InsCounter

## Intro
This is an instrument used for counting the total number of execute bytecode instructions.

## Input
Jar File

## Output
Edited Jar file that can output the numebr of execute bytecode instructions.

## Library
It is based on ASM. 

## Usage
	Step 1. 
	Put the <code>InsnCounter.jar</code>, <code>asm-all-5.0.3.jar</code>, and the jar file you want to count in the same directory.

	For example: 
	Here we have: <code>InsnCounter.jar</code>, <code>asm-all-5.0.3.jar</code>, <code>Hello.jar</code>

	Step 2.
	Inside the directory, run <code>Java -jar InsnCounter.jar <Your Jar file> </code>

	For here, we run <code> Java -jar InsnCounter.jar Hello.jar </code>

	Step 3.
	You will find that there is a new folder created under the directory, called "outJar". All the edited bytecode are shown there.

	To build artifacts, just chang to the "outJar" directory and run <code> jar </code> command.

	For here, we run:
	<code> cd outJar </code>
	<code> jar cf newHello.jar com </code>

	Then we get the goal.
	

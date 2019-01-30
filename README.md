<img src="asm.png" align="right" width=25% height=25%>

# InsCounter

[![Language](https://img.shields.io/badge/language-Java-red.svg)](http://www.cplusplus.com/) 
[![license](https://img.shields.io/badge/license-MIT-green.svg)]()


## Introduction

This is an instrument used for counting the total number of execute bytecode instructions.

## Input

Your Jar File.

## Output

Edited Jar file that can output the numebr of execute bytecode instructions.

## Library

It is based on [ASM](). 

## Usage

**Step 1**

Put `InsnCounter.jar`, `asm-all-5.0.3.jar`, and the jar file you want to count in the same directory.

For example: 
Here we have: `InsnCounter.jar`, `asm-all-5.0.3.jar`, `Hello.jar` in one directory.

**Step 2**

Inside the directory, run 

`Java -jar InsnCounter.jar <Your Jar file>`

For here, we run

`Java -jar InsnCounter.jar Hello.jar`

**Step 3**

You will find that there is a new folder called "outJar" created under the directory. All the edited bytecode are shown there.

To build artifacts, just chang your directory to "outJar" and run jar command.

For here, we run:

`cd outJar `

`jar cf newHello.jar com `

Then we get the goal.

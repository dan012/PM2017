all: lightsControl.hex

lightsControl.hex: lightsControl.elf
	avr-objcopy  -j .text -j .data -O ihex $^ $@
	avr-size lightsControl.elf

lightsControl.elf: lightsControl.c usart.c
	avr-g++ -mmcu=atmega324a -Os -Wall -o $@ $^

clean:
	rm -rf lightsControl.elf lightsControl.hex

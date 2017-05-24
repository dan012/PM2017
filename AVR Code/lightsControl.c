#define F_CPU 16000000

#include <avr/io.h>
#include <util/delay.h>

#include <stdio.h>

#include "usart.h"

#define DELAY_UNIT	62

int siren_status, lightbar_status, strobes_status, rearAlley_status, sideAlley_status;

void resetLights()
{
	PORTC &= (1 << 2); //leave LowBeams on
	
	PORTD &= ~(28); //reset PD2, PD3, PD4
	
	/* Reset variables */
	siren_status = 0;
	lightbar_status = 0;
	strobes_status = 0;
	rearAlley_status = 0;
	sideAlley_status = 0;
}

void initSystem()
{
	/* Set PORTC as output */
	DDRC = 0xFF;

	/* Set PORTD as output */
	DDRD = 0xFF;
	
	/* Reset PORTC pins */
	PORTC = 0x00;

	/* Reset PORTD pins */
	PORTD = 0x00;

	/* Turn on low beams */
	PORTC |= (1 << 2);

	/* Turn on alley lights (TO BE CHANGED WHEN BLUETOOTH IS AVAILABLE)
	PORTC |= (1 << 7); // left alley lights
	PORTD |= (1 << 2); // right alley lights
	PORTD |= (1 << 3); // rear alley lights
	*/
	
	/* Reset variables */
	resetLights();
}

void updateLightbar(int step)
{
	if(step < 4)
	{
		PORTC ^= (1 << 0); // FLASH LEFT LIGHTBAR

		PORTC &= ~(1 << 1); // TURN OFF RIGHT LIGHTBAR
	}
	else
	{
		PORTC &= ~(1 << 0); // TURN OFF LEFT LIGHTBAR

		PORTC ^= (1 << 1); // FLASH RIGHT LIGHTBAR
	}
}

void updateGrilleAndRearStrobe(int step)
{
	if(step < 4)
	{
		PORTC ^= (1 << 6); // FLASH RIGHT SIDE

		PORTC &= ~(1 << 5); // TURN OFF LEFT SIDE
	}
	else
	{
		PORTC &= ~(1 << 6); // TURN OFF RIGHT SIDE

		PORTC ^= (1 << 5); // FLASH LEFT SIDE
	}
}

void updateWigWags(int step)
{
	if(step == 0)
	{
		PORTC ^= (1 << 3 | 1 << 4);
	}
}

void delayCallback(int *seq_count)
{
	if(lightbar_status == 1)
	{
		updateLightbar(*seq_count);
		updateWigWags(*seq_count);
	}
	
	if(strobes_status == 1)
	{
		updateGrilleAndRearStrobe(*seq_count);
	}


	(*seq_count)++;
}

int main(void)
{
	initSystem();
	USART0_init();
	
	int seq_count = 0;
	char ch;

	while(1)
	{
		if(UCSR0A & (1<<RXC0)) { //non-blocking USART receive
			ch = UDR0;
			
			if(ch == '0')
			{
				resetLights();	
			}
			else if(ch == '1')
			{
				lightbar_status = 1;
				
				if(strobes_status == 0)
					strobes_status = 1;
			}
			else if(ch == '2')
			{
				strobes_status = 1 - strobes_status;
				if(strobes_status == 0)
				{
					PORTC &= ~(1 << 5 | 1 << 6);
				}
				
				if(lightbar_status == 0)
				{
					lightbar_status = 1;
				}
			}
			else if(ch == '3')
			{
				rearAlley_status = 1 - rearAlley_status;
				
				if(rearAlley_status == 0)
				{
					PORTD &= ~(1 << 3);
				}
				else
				{
					PORTD |= (1 << 3);
				}
			}
			else if(ch == '4')
			{
				sideAlley_status = (sideAlley_status + 1) % 4;
				
				if(sideAlley_status == 1)
				{
					PORTC |= (1 << 7);
				}
				else if(sideAlley_status == 2)
				{
					PORTC &= ~(1 << 7);
					PORTD |= (1 << 2);
				}
				else if(sideAlley_status == 3)
				{
					PORTC |= (1 << 7);
					PORTD |= (1 << 2);
				}
				else
				{
					PORTC &= ~(1 << 7);
					PORTD &= ~(1 << 2);
				}
			}
			else if(ch == 'S')
			{
				siren_status = (siren_status + 1) % 4;
				
				if(siren_status > 0 && lightbar_status == 0)
				{
					lightbar_status = 1;
					
					if(strobes_status == 0)
					{
						strobes_status = 1;
					}
				}
			}
		}
		
		delayCallback(&seq_count);
		
		seq_count = seq_count % 8;
		
		_delay_ms(DELAY_UNIT);
	}
	
	return 0;
}

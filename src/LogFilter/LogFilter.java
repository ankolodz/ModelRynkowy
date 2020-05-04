package LogFilter;

import java.util.Scanner;

public class LogFilter {
	
	public static void main(String[] args) {
		int choice = 0;
		int priorityChoice = 0;
		Filter filter = new Filter();
		
		while(choice !=  -1){
			System.out.println("To make a choice please type one of the numbers on the left.");
			System.out.println("Choose file to filter:");
			System.out.println("1. File from agent priorities");
			System.out.println("2. roads_prices.xml- prices for roads");
			System.out.println("3. roads_reservations.xml - reservations and blocked roads count");
			System.out.println("4. current_to_reserved.xml - average current price to average reserved price");
			System.out.println("5. exit");
			
			Scanner sc = new Scanner(System.in);
			if(sc.hasNext()){
				choice = sc.nextInt();
			}
			switch(choice){
			
				case 1:
					System.out.println("Which file to filter:");
					System.out.println("1. agnets_money.xml - cost for every priority of agents");
					System.out.println("2. agents_time.xml - time for every priority of agents");
					System.out.println("3. agents_distance.xml - distance for priority type of agents");
					System.out.println("4. exit");
					priorityChoice = sc.nextInt();
					
					switch (priorityChoice){
						case 1:
							System.out.println("Chosen file to filter: agents_money");
							filter.agentMoneyPriorityFilter();
							break;
							
						case 2:
							System.out.println("Chosen file to filter: agents_time");
							filter.agentTimePriorityFilter();
							break;
							
						case 3:
							System.out.println("Chosen file to filter: agents_distance");
							filter.agentDistancePriorityFilter();
							break;
							
						case 4:
							System.out.println("Exiting priority files filtering");
							break;
							
							
						default:
							System.out.println("Wrong number. Choose numbers from 1 to 5");
							continue;
					}
					break;
					
				case 2:
					System.out.println("Chosen file to filter: roads_prices.xml");
					filter.roadPriceFilter();
					break;
					
				case 3:
					System.out.println("Chosen file to filter: roads_reservations.xml");
					filter.roadReservationsFilter();
					break;
					
				case 4:
					System.out.println("Chosen file to filter: current_to_reserved.xml");
					filter.currentToReservedFilter();
					break;
					
				case 5:
					System.out.println("Exiting LogFilter");
					choice = -1;
					break;
					
				default:
					System.out.println("Wrong number. Choose numbers from 1 to 5");
					continue;
					
			}
			
		}
		
	}

}

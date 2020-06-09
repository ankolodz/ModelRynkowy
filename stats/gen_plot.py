import os
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

FIG_SIZE = (13, 7)
FONT_SIZE = 15

source_dir = ''
dest_dir = 'plots'
stats_dir = 'stats'
if not os.path.exists(dest_dir):
    os.mkdir(dest_dir)

if not os.path.exists(stats_dir):
    os.mkdir(stats_dir)

plt.rcParams.update({'font.size': FONT_SIZE})

def print_agents_prop(value_name, data):
    figure = plt.figure(figsize=FIG_SIZE)
    plt.plot(data['   step'], data[' WHOLE'], label="Whole")
    plt.plot(data['   step'], data[' ONE'], label="One")
    plt.plot(data['   step'], data[' TWO'], label="Two")
    plt.plot(data['   step'], data[' THREE'], label="Three")
    plt.legend()
    plt.grid()
    plt.xlabel("step")
    plt.ylabel(value_name)
    plt.title('average {}'.format(value_name))
    plt.savefig('{}/actor_{}.png'.format(dest_dir, value_name))
	
def process_prop(prop_name):
    data = pd.read_csv('{}agents_{}.txt'.format(source_dir, prop_name), '\t')
    data.drop('   step', axis=1).describe().to_excel('{}/actor_{}.xlsx'.format(stats_dir, prop_name))
    print_agents_prop(prop_name, data)
    return(1)

agents_props = ['distance', 'money', 'time', 'travel_ended', 'waiting']

list(map(process_prop, agents_props))

def print_priority_agents_prop(value_name, data):
    figure = plt.figure(figsize=FIG_SIZE)
    plt.plot(data['   step'], data[' time'], label="Time")
    plt.plot(data['   step'], data[' cost'], label="Cost")
    plt.plot(data['   step'], data['distance'], label="Distance")
    plt.legend()
    plt.grid()
    plt.xlabel("step")
    plt.ylabel(value_name)
    plt.title('average {}'.format(value_name))
    plt.savefig('{}/priority_actor_{}.png'.format(dest_dir, value_name))
	
def process_priority_prop(prop_name):
    data = pd.read_csv('{}priority/agents_{}.txt'.format(source_dir, prop_name), '\t')
    data.drop('   step', axis=1).describe().to_excel('{}/priority_actor_{}.xlsx'.format(stats_dir, prop_name))
    print_priority_agents_prop(prop_name, data)
    return(1)

priority_agents_props = ['distance', 'money', 'time']

list(map(process_priority_prop, priority_agents_props))


def print_current_to_reserved(data):
    figure = plt.figure(figsize=FIG_SIZE)
    plt.plot(data['step'], data[' currentPrice'], label="Current")
    plt.plot(data['step'], data[' reservedPrice'], label="Reserved")
    plt.legend()
    plt.grid()
    plt.xlabel('step')
    plt.ylabel('Price')
    plt.title('Relation between current and reserved price')
    plt.savefig('{}/current_to_reserved.png'.format(dest_dir))	
    
current_to_reserved_data = pd.read_csv('{}Current_to_reserved.txt'.format(source_dir), '\t')
print_current_to_reserved(current_to_reserved_data)

def print_roads_price(data):
    figure, (a0, a1) = plt.subplots(2, 1, gridspec_kw = {'height_ratios':[3, 1]}, figsize=FIG_SIZE, sharex=True)
    a0.plot(data['step'], data[' averageRoadPrice'], label="Average")
    a0.plot(data['step'], data[' maxPrice'], label="Max")
    a0.legend()
    a0.grid()
    a0.set_ylabel('Price')
    a0.set_title('Road price')
    a1.plot(data['step'], data[' variation'], label="Variation")
    a1.legend()
    a1.grid()
    a1.set_xlabel('step')
    a1.set_ylabel('Variation')
    plt.savefig('{}/roads_price.png'.format(dest_dir))

roads_price_data = pd.read_csv('{}roads_price.txt'.format(source_dir), '\t')
print_roads_price(roads_price_data)

def print_roads_reservations(data):
    figure, (a0, a1) = plt.subplots(2, 1, gridspec_kw = {'height_ratios':[3, 1]}, figsize=FIG_SIZE, sharex=True)
    a0.plot(data['step'], data[' averageReservationCount'], label="Average")
    a0.plot(data['step'], data[' blockedRoads'], label="Blocked roads")
    a0.plot(data['step'], data[' maxReservations'], label="Max")
    a0.legend()
    a0.grid()
    a0.set_ylabel('Price')
    a0.set_title('Road reservation')
    a1.plot(data['step'], data[' variation'], label="Variation")
    a1.legend()
    a1.grid()
    a1.set_xlabel('step')
    a1.set_ylabel('Variation')
    plt.savefig('{}/roads_reservation.png'.format(dest_dir))
    

roads_reservations_data = pd.read_csv('{}roads_reservations.txt'.format(source_dir), '\t')
print_roads_reservations(roads_reservations_data)

def roads_reservation_quantiles(data):
    figure = plt.figure(figsize=FIG_SIZE)
    plt.plot(data['step'], data[' five'], label="1/2")
    plt.plot(data['step'], data[' six'], label="6/10")
    plt.plot(data['step'], data[' seven'], label="7/10")
    plt.plot(data['step'], data[' eight'], label="8/10")
    plt.plot(data['step'], data[' nine'], label="9/10")
    plt.legend()
    plt.grid()
    plt.xlabel('step')
    plt.ylabel('Reservation count on roads')
    plt.title('Road reservation quantiles')
    plt.savefig('{}/roads_reservation_quantiles.png'.format(dest_dir))
    
roads_reservation_quantiles_data = pd.read_csv('{}roads_reservations_quantiles.txt'.format(source_dir), '\t')
roads_reservation_quantiles(roads_reservation_quantiles_data)

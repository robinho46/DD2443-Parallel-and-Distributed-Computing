import os
import re
import matplotlib.pyplot as plt

# Function to extract measurement times from a file
def extract_measurement_times(file_path):
    times = {}
    current_threads = None
    with open(file_path, 'r') as file:
        for line in file:
            # Match the thread count line
            if re.match(r'^\d+ -+', line):
                current_threads = int(line.split()[0])
                times[current_threads] = []
            # Match measurement times
            elif 'Measurement time:' in line:
                match = re.search(r'Measurement time: (\d+)', line)
                if match and current_threads is not None:
                    times[current_threads].append(int(match.group(1)))
    return times

# File paths for each configuration
files = {
    #"Lock": "lock.txt",
    "Local": "local.txt",
    "Global": "global.txt"
}

# Extract times and calculate averages, speedups, and efficiencies
def calculate_metrics(file_path):
    times = extract_measurement_times(file_path)
    avg_times = {threads: sum(times[threads]) / len(times[threads]) for threads in times}
    
    # Calculate speedups and efficiencies based on single-thread time
    single_thread_time = avg_times[1]
    speedups = {threads: single_thread_time / avg_time for threads, avg_time in avg_times.items()}
    efficiencies = {threads: (speedup / threads) * 100 for threads, speedup in speedups.items()}
    return avg_times, speedups, efficiencies

# Calculate metrics for each configuration
avg_times_all = {}
speedups_all = {}
efficiencies_all = {}
for config, file_path in files.items():
    avg_times, speedups, efficiencies = calculate_metrics(file_path)
    avg_times_all[config] = avg_times
    speedups_all[config] = speedups
    efficiencies_all[config] = efficiencies

# Plot average measurement times
plt.figure(figsize=(10, 6))
for config, avg_times in avg_times_all.items():
    plt.plot(avg_times.keys(), avg_times.values(), marker='o', linestyle='-', label=config)
plt.title('Average Measurement Time vs Number of Threads')
plt.xlabel('Number of Threads')
plt.ylabel('Average Measurement Time (nanoseconds)')
plt.legend()
plt.grid(True)
plt.show()

# Plot speedups
plt.figure(figsize=(10, 6))
for config, speedups in speedups_all.items():
    plt.plot(speedups.keys(), speedups.values(), marker='o', linestyle='-', label=config)
plt.title('Speedup vs Number of Threads')
plt.xlabel('Number of Threads')
plt.ylabel('Speedup')
plt.legend()
plt.grid(True)
plt.show()

# Plot efficiencies
plt.figure(figsize=(10, 6))
for config, efficiencies in efficiencies_all.items():
    plt.plot(efficiencies.keys(), efficiencies.values(), marker='o', linestyle='-', label=config)
plt.title('Efficiency (Accuracy) vs Number of Threads')
plt.xlabel('Number of Threads')
plt.ylabel('Efficiency (%)')
plt.legend()
plt.grid(True)
plt.show()

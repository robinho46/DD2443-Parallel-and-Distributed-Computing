import re
import os
import numpy as np
import matplotlib.pyplot as plt

# List the four files.
file_list = [
    #'Normal_Global_110.txt',
    #'Normal_Global_118.txt',
    #'Uniform_Global_110.txt',
    #'Uniform_Global_118.txt',
    #'Normal_Local_110.txt',
    'Uniform_Local_118.txt',
    #'Normal_Local_118.txt',
    'Uniform_Local_110.txt'
]

# Regular expressions for:
# - Block header: a line starting with a number and some dashes.
# - Measurement time and discrepancy lines.
block_header_pattern = re.compile(r"^(\d+)\s*-+")
time_pattern = re.compile(r"Measurement time:\s*(\d+)")
disc_pattern = re.compile(r"Measurement discrepancy:\s*(\d+)")

# Data storage:
# results[file_label] will be a dictionary mapping thread count (as int)
# to a dictionary with two lists: one for mean times and one for mean discrepancies.
results = {}

for filepath in file_list:
    file_label = os.path.splitext(os.path.basename(filepath))[0]
    results[file_label] = {}
    
    current_thread = None
    current_times = []
    current_discs = []
    
    with open(filepath, "r") as f:
        for line in f:
            line = line.strip()
            
            # Check if the line is a block header.
            header_match = block_header_pattern.match(line)
            if header_match:
                # Save previous block if one exists.
                if current_thread is not None:
                    # Use the block's average (if any measurement times/discs found).
                    avg_time = np.mean(current_times) if current_times else 0
                    avg_disc = np.mean(current_discs) if current_discs else 0
                    if current_thread not in results[file_label]:
                        results[file_label][current_thread] = {"times": [], "discs": []}
                    results[file_label][current_thread]["times"].append(avg_time)
                    results[file_label][current_thread]["discs"].append(avg_disc)
                    
                # Start a new block.
                current_thread = int(header_match.group(1))
                current_times = []
                current_discs = []
                continue

            # Otherwise, check for measurement time/discrepancy lines.
            t_match = time_pattern.search(line)
            if t_match:
                current_times.append(int(t_match.group(1)))
                continue

            d_match = disc_pattern.search(line)
            if d_match:
                current_discs.append(int(d_match.group(1)))
                continue

        # At end-of-file, save the last block.
        if current_thread is not None:
            avg_time = np.mean(current_times) if current_times else 0
            avg_disc = np.mean(current_discs) if current_discs else 0
            if current_thread not in results[file_label]:
                results[file_label][current_thread] = {"times": [], "discs": []}
            results[file_label][current_thread]["times"].append(avg_time)
            results[file_label][current_thread]["discs"].append(avg_disc)

# For each file and each thread count, average over block means if more than one exists.
# We'll then plot the final values.

# Prepare containers for plotting.
# For the time plot: each file will have x (threads) and y (mean measurement times).
plot_data_time = {}
plot_data_disc = {}

for file_label, thread_data in results.items():
    threads = sorted(thread_data.keys())
    avg_times = []
    avg_discs = []
    for t in threads:
        # Calculate overall mean for this thread count.
        mean_time = np.mean(thread_data[t]["times"])
        mean_disc = np.mean(thread_data[t]["discs"])
        avg_times.append(mean_time)
        avg_discs.append(mean_disc)
    plot_data_time[file_label] = (threads, avg_times)
    plot_data_disc[file_label] = (threads, avg_discs)

# ----------------
# Plot Mean Measurement Time vs. Thread Count for the 4 methods.
plt.figure(figsize=(10, 5))
for file_label, (threads, times) in plot_data_time.items():
    plt.plot(threads, times, marker='o', linestyle='-', label=file_label)
plt.xlabel("Thread Count")
plt.ylabel("Mean Measurement Time")
plt.title("Mean Measurement Time vs. Thread Count")
plt.grid(True)
plt.legend()
plt.tight_layout()
plt.show()

# ----------------
# Plot Mean Measurement Discrepancy vs. Thread Count for the 4 methods.
plt.figure(figsize=(10, 5))
for file_label, (threads, discs) in plot_data_disc.items():
    plt.plot(threads, discs, marker='s', linestyle='-', label=file_label)
plt.xlabel("Thread Count")
plt.ylabel("Mean Measurement Discrepancy")
plt.title("Mean Measurement Discrepancy vs. Thread Count")
plt.grid(True)
plt.legend()
plt.tight_layout()
plt.show()

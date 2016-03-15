## Gnuplot script
## author: Kamel Aouiche
## 
reset
set macros
post="set terminal postscript eps enhanced color \"Arial\" 20"

T1="COSINE"
T2="TANIMOT0"

##Dataset

DATASET='similarity_exp_usincome.txt'

#every I:J:K:L:M:N
# I 	Line increment
# J 	Data block increment
# K 	The first line
# L 	The first data block
# M 	The last line
# N 	The last data block

#for each block plot each NBLimits line
#every NBLimits:1::1::1

SimSample1points="every :::0::0" #block 1
#SimSample2points="every :::1::1" #block 2

#Settings
set nologscale x
set nologscale y
set notitle
set ylabel "MLA cost" 

set boxwidth 0.9 absolute
set style fill   pattern  1 border -1
set style histogram clustered gap 1 title  offset character 0, 0, 0
set style data histograms
#set xtics border in scale 1,0.5 nomirror rotate by -45  offset character 0, 0, 0 

set xtics   (T1 0, T2 1)

plot	DATASET @SimSample1points using 3:xtic(1) ti col, '' u 5 ti col, '' u 7 ti col, '' u 9 ti col, '' u 11 ti col

@post
set output "sim_usincome.eps"
replot
! epstopdf "sim_usincome.eps"

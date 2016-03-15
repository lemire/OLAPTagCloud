## Gnuplot script
## author: Kamel Aouiche
## 
#### Help to easily read this script
# D means dimensios
# CD# of dimension number #
# StyleD# associated style to lines and points used for plotting data
# NBLimits max number of limit values for the current test L# is the #th value of limit values
# NBTagCloudSizes max number of tag ccloud sizes for the current test, TCS# means the #th Tag-cloud size value
# fn means false positive and fp false positive


reset
set macros
post="set terminal postscript eps enhanced color \"Arial\" 22"

### 4 dimensions
StyleD1="with linespoints lw 10 linetype 1"
StyleD2="with linespoints lw 10 linetype 3"
StyleD3="with linespoints lw 10 linetype 4"
StyleD4="with linespoints lw 10 linetype 9"

### 4 dimensions
StyleDP1="with points ps 2 pointtype  1"
StyleDP2="with points ps 2 pointtype  5"
StyleDP3="with points ps 2 pointtype  7"
StyleDP4="with points ps 2 pointtype  9"

#These values are in your data file
CD1="Country of birth (43)"
CD2="Age (91)"
CD3="Capital losses (1478)"
CD4="Household (99800)"

### NB of limit and Tag-cloud size values
NBLimits=5
NBTagCloudSizes=4

##Dataset

DATASET='iceberg_usincome.txt'

#every I:J:K:L:M:N
# I 	Line increment
# J 	Data block increment
# K 	The first line
# L 	The first data block
# M 	The last line
# N 	The last data block

#for each block plot each NBLimits line
#every NBLimits:1::1::1

D1pointsTCS1="every NBTagCloudSizes::0:0::0" #block 1 TCS VALUE 1
D2pointsTCS1="every NBTagCloudSizes::0:1::1" #block 2
D3pointsTCS1="every NBTagCloudSizes::0:2::2" #block 3
D4pointsTCS1="every NBTagCloudSizes::0:3::3" #block 4

D1pointsTCS2="every NBTagCloudSizes::1:0::0" #block 1 TCS VALUE 2
D2pointsTCS2="every NBTagCloudSizes::1:1::1" #block 2
D3pointsTCS2="every NBTagCloudSizes::1:2::2" #block 3
D4pointsTCS2="every NBTagCloudSizes::1:3::3" #block 4

D1pointsTCS3="every NBTagCloudSizes::2:0::0" #block 1 TCS VALUE 3
D2pointsTCS3="every NBTagCloudSizes::2:1::1" #block 2
D3pointsTCS3="every NBTagCloudSizes::2:2::2" #block 3
D4pointsTCS3="every NBTagCloudSizes::2:3::3" #block 4

D1pointsTCS4="every NBTagCloudSizes::NBTagCloudSizes-1:0::0" #block 1 TCS VALUE 4
D2pointsTCS4="every NBTagCloudSizes::NBTagCloudSizes-1:1::1" #block 2
D3pointsTCS4="every NBTagCloudSizes::NBTagCloudSizes-1:2::2" #block 3
D4pointsTCS4="every NBTagCloudSizes::NBTagCloudSizes-1:3::3" #block 4

D1pointsTCS4="every NBTagCloudSizes::NBTagCloudSizes-1:0::0" #block 1 TCS VALUE 1
D2pointsTCS4="every NBTagCloudSizes::NBTagCloudSizes-1:1::1" #block 2
D3pointsTCS4="every NBTagCloudSizes::NBTagCloudSizes-1:2::2" #block 3
D4pointsTCS4="every NBTagCloudSizes::NBTagCloudSizes-1:3::3" #block 4


D1pointsL1="every ::0:0:NBTagCloudSizes-1:0" #block 1 Limit VALUE 1
D2pointsL1="every ::0:1:NBTagCloudSizes-1:1" #block 2
D3pointsL1="every ::0:2:NBTagCloudSizes-1:2" #block 3
D4pointsL1="every ::0:3:NBTagCloudSizes-1:3" #block 4

D1pointsL5="every ::(NBLimits-1)*(NBLimits-1):0:(NBLimits-1)*(NBLimits-1)+NBTagCloudSizes-1:0" #block 1 Limit VALUE 5
D2pointsL5="every ::(NBLimits-1)*(NBLimits-1):1:(NBLimits-1)*(NBLimits-1)+NBTagCloudSizes-1:1" #block 2
D3pointsL5="every ::(NBLimits-1)*(NBLimits-1):2:(NBLimits-1)*(NBLimits-1)+NBTagCloudSizes-1:2" #block 3
D4pointsL5="every ::(NBLimits-1)*(NBLimits-1):3:(NBLimits-1)*(NBLimits-1)+NBTagCloudSizes-1:3" #block 4



D1points="every :::0::0" #block 1
D2points="every :::1::1" #block 2
D3points="every :::2::2" #block 3
D4points="every :::3::3" #block 4


#Settings
#set key left bottom
set nologscale x
set nologscale y
set notitle
set style data linespoints
set grid
set format x "%.0s"; 

###  Entropy vs. Limit (TCS is invariant values 1)                          ###
#set xlabel "{/Times-Italic Iceberg limit (x10^3)}"
#set ylabel "{/Times-Italic Entropy}"
#plot	DATASET @D1pointsTCS1 using 1:8 ti CD1 @StyleD1,\
#	DATASET @D2pointsTCS1 using 1:8 ti CD2 @StyleD2,\
#	DATASET @D3pointsTCS1 using 1:8 ti CD3 @StyleD3,\
#	DATASET @D4pointsTCS1 using 1:8 ti CD4  @StyleD4
#@post
#set output "entropy_vs_limit_TCS1_usincome.eps"
#replot
#! epstopdf "entropy_vs_limit_TCS1_usincome.eps"

###  Entropy vs. Limit (TCS is invariant value 4)                          ###
#set xlabel "{/Times-Italic Iceberg limit (x10^3)}"
#set ylabel "{/Times-Italic Entropy}"
#plot	DATASET @D1pointsTCS4 using 1:8 ti CD1 @StyleD1,\
#	DATASET @D2pointsTCS4 using 1:8 ti CD2 @StyleD2,\
#	DATASET @D3pointsTCS4 using 1:8 ti CD3 @StyleD3,\
#	DATASET @D4pointsTCS4 using 1:8 ti CD4  @StyleD4
#@post
#set output "entropy_vs_limit_TCS4_usincome.eps"
#replot
#! epstopdf "entropy_vs_limit_TCS4_usincome.eps"


###  Entropy vs. TSC (limit is invariant value l1)                          ###
set xlabel "{/Times-Italic Tag-cloud size}"
set ylabel "{/Times-Italic Entropy}"
set key left top
plot	DATASET @D1pointsL1 using 2:8 ti CD1 @StyleD1,\
	DATASET @D2pointsL1 using 2:8 ti CD2 @StyleD2,\
	DATASET @D3pointsL1 using 2:8 ti CD3 @StyleD3,\
	DATASET @D4pointsL1 using 2:8 ti CD4  @StyleD4,\
	log(x)/log(2) ti "Maximum entropy value"
@post
set output "entropy_vs_tcs_limit1_usincome.eps"
replot
! epstopdf "entropy_vs_tcs_limit1_usincome.eps"

###  Entropy vs. TSC (limit is invariant value l5)                          ###
set xlabel "{/Times-Italic Tag-cloud size}"
set ylabel "{/Times-Italic Entropy}"
set key left top
plot	DATASET @D1pointsL5 using 2:8 ti CD1 @StyleD1,\
	DATASET @D2pointsL5 using 2:8 ti CD2 @StyleD2,\
	DATASET @D3pointsL5 using 2:8 ti CD3 @StyleD3,\
	DATASET @D4pointsL5 using 2:8 ti CD4  @StyleD4,\
	log(x)/log(2) ti "Maximum entropy value"
@post
set output "entropy_vs_tcs_limit5_usincome.eps"
replot
! epstopdf "entropy_vs_tcs_limit5_usincome.eps"

#####################################################################################

###  False positive vs. TCS (limit is invariant value l1)                          ###
#set xlabel "{/Times-Italic Tag-cloud size}"
#set ylabel "{/Times-Italic False positive}"
#plot	DATASET @D1pointsL1 using 2:10 ti CD1 @StyleD1,\
#	DATASET @D2pointsL1 using 2:10 ti CD2 @StyleD2,\
#	DATASET @D3pointsL1 using 2:10 ti CD3 @StyleD3,\
#	DATASET @D4pointsL1 using 2:10 ti CD4  @StyleD4
#@post
#set output "fp_vs_tcs_limit1_usincome.eps"
#replot
#! epstopdf "fp_vs_tcs_limit1_usincome.eps"

###  False positive vs. TCS (limit is invariant value l5)                          ###
#set xlabel "{/Times-Italic Tag-cloud size}"
#set ylabel "{/Times-Italic False positive}"
#plot	DATASET @D1pointsL5 using 2:10 ti CD1 @StyleD1,\
#	DATASET @D2pointsL5 using 2:10 ti CD2 @StyleD2,\
#	DATASET @D3pointsL5 using 2:10 ti CD3 @StyleD3,\
#	DATASET @D4pointsL5 using 2:10 ti CD4  @StyleD4
#@post
#set output "fp_vs_tcs_limit5_usincome.eps"
#replot
#! epstopdf "fp_vs_tcs_limit5_usincome.eps"

###  False positive vs. Limit (TCS is invariant value 1)                          ###
#set yrange [0:1]
set xlabel "{/Times-Italic Iceberg limit (x10^3)}"
set ylabel "{/Times-Italic False positive}"
plot	DATASET @D1pointsTCS1 using 1:10 ti CD1 @StyleD1,\
	DATASET @D3pointsTCS1 using 1:10 ti CD3 @StyleD3
@post
set output "fp_vs_limit_TCS1_usincome.eps"
replot
! epstopdf "fp_vs_limit_TCS1_usincome.eps"
#	DATASET @D2pointsTCS1 using 1:10 ti CD2 @StyleD2,\
#, \
#	DATASET @D4pointsTCS1 using 1:10 ti CD4  @StyleD4

###  False positive vs. Limit (TCS is invariant value 4)                          ###
#set yrange [0:1]
set xlabel "{/Times-Italic Iceberg limit (x10^3)}"
set ylabel "{/Times-Italic False positive}"
plot	DATASET @D1pointsTCS4 using 1:10 ti CD1 @StyleD1,\
	DATASET @D2pointsTCS4 using 1:10 ti CD2 @StyleD2,\
	DATASET @D3pointsTCS4 using 1:10 ti CD3 @StyleD3,\
	DATASET @D4pointsTCS4 using 1:10 ti CD4  @StyleD4
@post
set output "fp_vs_limit_TCS4_usincome.eps"
replot
! epstopdf "fp_vs_limit_TCS4_usincome.eps"

#####################################################################################

###  False negative vs. Limit (TCS is invariant value 1)                          ###
#set yrange [0:1]
set xlabel "{/Times-Italic Iceberg limit (x10^3)}"
set ylabel "{/Times-Italic False negative}"
plot	DATASET @D1pointsTCS1 using 1:9 ti CD1 @StyleD1,\
	DATASET @D3pointsTCS1 using 1:9 ti CD3 @StyleD3
@post
set output "fn_vs_limit_TCS1_usincome.eps"
replot
! epstopdf "fn_vs_limit_TCS1_usincome.eps"
#	DATASET @D2pointsTCS1 using 1:9 ti CD2 @StyleD2,\
#,\
#	DATASET @D4pointsTCS1 using 1:9 ti CD4  @StyleD4

###  False negative vs. Limit (TCS is invariant value 4)                          ###
#set yrange [0:1]
set xlabel "{/Times-Italic Iceberg limit (x10^3)}"
set ylabel "{/Times-Italic False negative}"
plot	DATASET @D1pointsTCS4 using 1:9 ti CD1 @StyleD1,\
	DATASET @D2pointsTCS4 using 1:9 ti CD2 @StyleD2,\
	DATASET @D3pointsTCS4 using 1:9 ti CD3 @StyleD3,\
	DATASET @D4pointsTCS4 using 1:9 ti CD4  @StyleD4
@post
set output "fn_vs_limit_TCS4_usincome.eps"
replot
! epstopdf "fn_vs_limit_TCS4_usincome.eps"

###  False negative vs. Limit (TCS is invariant value l1)                          ###
#set xlabel "{/Times-Italic Tag-cloud size}"
#set ylabel "{/Times-Italic False negative}"
#plot	DATASET @D1pointsL1 using 2:9 ti CD1 @StyleD1,\
#	DATASET @D2pointsL1 using 2:9 ti CD2 @StyleD2,\
#	DATASET @D3pointsL1 using 2:9 ti CD3 @StyleD3,\
#	DATASET @D4pointsL1 using 2:9 ti CD4  @StyleD4
#@post
#set output "fn_vs_tcs_limit1_usincome.eps"
#replot
#! epstopdf "fn_vs_tcs_limit1_usincome.eps"

###  False negative vs. TCS (Limit is invariant value l5)                          ###
#set xlabel "{/Times-Italic Tag-cloud size}"
#set ylabel "{/Times-Italic False negative}"
#plot	DATASET @D1pointsL5 using 2:9 ti CD1 @StyleD1,\
#	DATASET @D2pointsL5 using 2:9 ti CD2 @StyleD2,\
#	DATASET @D3pointsL5 using 2:9 ti CD3 @StyleD3,\
#	DATASET @D4pointsL5 using 2:9 ti CD4  @StyleD4
#@post
#set output "fn_vs_tcs_limit5_usincome.eps"
#replot
#! epstopdf "fn_vs_tcs_limit5_usincome.eps"


#####################################################################################

### Icebreg processing time vs. Limit (TCS is invariant value 1)                          ###
set yrange [:]
set xlabel "{/Times-Italic Iceberg limit (x10^3)}"
set ylabel "{/Times-Italic Processing time (ms)}"
plot	DATASET @D1pointsTCS1 using 1:7 ti CD1 @StyleD1,\
	DATASET @D2pointsTCS1 using 1:7 ti CD2 @StyleD2,\
	DATASET @D3pointsTCS1 using 1:7 ti CD3 @StyleD3,\
	DATASET @D4pointsTCS1 using 1:7 ti CD4  @StyleD4
@post
set output "icebergtime_vs_limit_TCS1_usincome.eps"
replot
! epstopdf "icebergtime_vs_limit_TCS1_usincome.eps"


### Icebreg processing time vs. Limit (TCS is invariant value 4)                          ###
set xlabel "{/Times-Italic Iceberg limit (x10^3)}"
set ylabel "{/Times-Italic Processing time (ms)}"
plot	DATASET @D1pointsTCS4 using 1:7 ti CD1 @StyleD1,\
	DATASET @D2pointsTCS4 using 1:7 ti CD2 @StyleD2,\
	DATASET @D3pointsTCS4 using 1:7 ti CD3 @StyleD3,\
	DATASET @D4pointsTCS4 using 1:7 ti CD4  @StyleD4
@post
set output "icebergtime_vs_limit_TCS4_usincome.eps"
replot
! epstopdf "icebergtime_vs_limit_TCS4_usincome.eps"


###  Icebreg processing time vs. TCS (Limit is invariant value l1)                          ###
#set xlabel "{/Times-Italic Tag-cloud size}"
#set ylabel "{/Times-Italic Processing time (ms)}"
#plot	DATASET @D1pointsL1 using 2:7 ti CD1 @StyleD1,\
#	DATASET @D2pointsL1 using 2:7 ti CD2 @StyleD2,\
#	DATASET @D3pointsL1 using 2:7 ti CD3 @StyleD3,\
#	DATASET @D4pointsL1 using 2:7 ti CD4  @StyleD4
#@post
#set output "icebergtime_vs_tcs_limit1_usincome.eps"
#replot
#! epstopdf "icebergtime_vs_tcs_limit1_usincome.eps"

###  Icebreg processing time vs. TCS (Limit is invariant value l5)                          ###
#set xlabel "{/Times-Italic Tag-cloud size}"
#set ylabel "{/Times-Italic Processing time (ms)}"
#plot	DATASET @D1pointsL5 using 2:7 ti CD1 @StyleD1,\
#	DATASET @D2pointsL5 using 2:7 ti CD2 @StyleD2,\
#	DATASET @D3pointsL5 using 2:7 ti CD3 @StyleD3,\
#	DATASET @D4pointsL5 using 2:7 ti CD4  @StyleD4
#@post
#set output "icebergtime_vs_tcs_limit5_usincome.eps"
#replot
#! epstopdf "icebergtime_vs_tcs_limit5_usincome.eps"

#####################################################################################
### Processing time vs. Limit (TCS is invariant value 1)                          ###
#set xlabel "{/Times-Italic Iceberg limit (x10^3)}"
#set ylabel "{/Times-Italic Processing time (ms)}"
#plot	DATASET @D1pointsTCS1 using 1:6 ti CD1 @StyleD1,\
#	DATASET @D2pointsTCS1 using 1:6 ti CD2 @StyleD2,\
#	DATASET @D3pointsTCS1 using 1:6 ti CD3 @StyleD3,\
#	DATASET @D4pointsTCS1 using 1:6 ti CD4  @StyleD4
#@post
#set output "exacttime_vs_limit_TCS1_usincome.eps"
#replot
#! epstopdf "exacttime_vs_limit_TCS1_usincome.eps"


### Processing time vs. Limit (TCS is invariant value 4)                          ###
#set xlabel "{/Times-Italic Iceberg limit (x10^3)}"
#set ylabel "{/Times-Italic Processing time (ms)}"
#plot	DATASET @D1pointsTCS4 using 1:6 ti CD1 @StyleD1,\
#	DATASET @D2pointsTCS4 using 1:6 ti CD2 @StyleD2,\
#	DATASET @D3pointsTCS4 using 1:6 ti CD3 @StyleD3,\
#	DATASET @D4pointsTCS4 using 1:6 ti CD4 @StyleD4
#@post
#set output "exacttime_vs_limit_TCS4_usincome.eps"
#replot
#! epstopdf "exacttime_vs_limit_TCS4_usincome.eps"


###  Processing time vs. TCS (Limit is invariant value l1)                          ###
#set xlabel "{/Times-Italic Tag-cloud size}"
#set ylabel "{/Times-Italic Processing time (ms)}"
#plot	DATASET @D1pointsL1 using 2:6 ti CD1 @StyleD1,\
#	DATASET @D2pointsL1 using 2:6 ti CD2 @StyleD2,\
#	DATASET @D3pointsL1 using 2:6 ti CD3 @StyleD3,\
#	DATASET @D4pointsL1 using 2:6 ti CD4 @StyleD4
#@post
#set output "exacttime_vs_tcs_limit1_usincome.eps"
#replot
#! epstopdf "exacttime_vs_tcs_limit1_usincome.eps"


###  Processing time vs. TCS (Limit is invariant value l5)                          ###
#set xlabel "{/Times-Italic Tag-cloud size}"
#set ylabel "{/Times-Italic Processing time (ms)}"
#plot	DATASET @D1pointsL5 using 2:6 ti CD1 @StyleD1,\
#	DATASET @D2pointsL5 using 2:6 ti CD2 @StyleD2,\
#	DATASET @D3pointsL5 using 2:6 ti CD3 @StyleD3,\
#	DATASET @D4pointsL5 using 2:6 ti CD4  @StyleD4
#@post
#set output "exacttime_vs_tcs_limit5_usincome.eps"
#replot
#! epstopdf "exacttime_vs_tcs_limit5_usincome.eps"


#####################################################################################
### Gain in processing time vs. Limit (TCS is invariant value 1)                          ###
set yrange[*:*]
set xlabel "{/Times-Italic Iceberg limit (x10^3)}"
set ylabel "{/Times-Italic Gain in processing time (%)}"
set key left bottom
plot	DATASET @D1pointsTCS1 using 1:(abs($6-$7)*100.0)/$6 ti CD1 @StyleD1,\
	DATASET @D2pointsTCS1 using 1:(abs($6-$7)*100.0)/$6 ti CD2 @StyleD2,\
	DATASET @D3pointsTCS1 using 1:(abs($6-$7)*100.0)/$6 ti CD3 @StyleD3,\
	DATASET @D4pointsTCS1 using 1:(abs($6-$7)*100.0)/$6 ti CD4  @StyleD4
@post
set output "gain_vs_limit_TCS1_usincome.eps"
replot
! epstopdf "gain_vs_limit_TCS1_usincome.eps"

### Gain in processing time vs. Limit (TCS is invariant value 4)                          ###
set xlabel "{/Times-Italic Iceberg limit (x10^3)}"
set ylabel "{/Times-Italic Gain in processing time (%)}"
plot	DATASET @D1pointsTCS4 using 1:(abs($6-$7)*100.0)/$6 ti CD1 @StyleD1,\
	DATASET @D2pointsTCS4 using 1:(abs($6-$7)*100.0)/$6 ti CD2 @StyleD2,\
	DATASET @D3pointsTCS4 using 1:(abs($6-$7)*100.0)/$6 ti CD3 @StyleD3,\
	DATASET @D4pointsTCS4 using 1:(abs($6-$7)*100.0)/$6 ti CD4  @StyleD4
@post
set output "gain_vs_limit_TCS4_usincome.eps"
replot
! epstopdf "gain_vs_limit_TCS4_usincome.eps"

###  Gain in processing time vs. TCS (Limit is invariant value l1)                          ###
#set xlabel "{/Times-Italic Tag-cloud size}"
#set ylabel "{/Times-Italic Gain in processing time (%)}"
#plot	DATASET @D1pointsL1 using 2:(abs($6-$7)*100)/$6 ti CD1 @StyleD1,\
#	DATASET @D2pointsL1 using 2:(abs($6-$7)*100)/$6 ti CD2 @StyleD2,\
#	DATASET @D3pointsL1 using 2:(abs($6-$7)*100)/$6 ti CD3 @StyleD3,\
#	DATASET @D4pointsL1 using 2:(abs($6-$7)*100)/$6 ti CD4  @StyleD4
#@post
#set output "gain_vs_tcs_limit1_usincome.eps"
#replot
#! epstopdf "gain_vs_tcs_limit1_usincome.eps"


###  Gain in processing time vs. TCS (Limit is invariant value l5)                          ###
#set xlabel "{/Times-Italic Tag-cloud size}"
#set ylabel "{/Times-Italic Gain in processing time (%)}"
#plot	DATASET @D1pointsL5 using 2:(abs($6-$7)*100)/$6 ti CD1 @StyleD1,\
#	DATASET @D2pointsL5 using 2:(abs($6-$7)*100)/$6 ti CD2 @StyleD2,\
#	DATASET @D3pointsL5 using 2:(abs($6-$7)*100)/$6 ti CD3 @StyleD3,\
#	DATASET @D4pointsL5 using 2:(abs($6-$7)*100)/$6 ti CD4  @StyleD4
#@post
#set output "gain_vs_tcs_limit5_usincome.eps"
#replot
#! epstopdf "gain_vs_tcs_limit5_usincome.eps"

reset

set key left top
set nologscale x
set nologscale y
set notitle
set grid
#set xrange [0:1]
#set yrange [0:1]

###  False positive vs. entropy/log(TCS)                          ###
set xlabel "{/Times-Italic entropy/log(tag-cloud size)}"
set ylabel "{/Times-Italic False positive}"
plot	DATASET @D1points using ($8/(log($2)/log(2))):10 ti CD1 @StyleDP1,\
	DATASET @D2points using ($8/(log($2)/log(2))):10 ti CD2 @StyleDP2,\
	DATASET @D3points using ($8/(log($2)/log(2))):10 ti CD3 @StyleDP3,\
	DATASET @D4points using ($8/(log($2)/log(2))):10 ti CD4 @StyleDP4
@post
set output "fp_vs_entropy_usincome.eps"
replot
! epstopdf "fp_vs_entropy_usincome.eps"

###  False negative vs. entropy/log(TCS)                          ###
set xlabel "{/Times-Italic entropy/log(tag-cloud size)}"
set ylabel "{/Times-Italic False negative}"
plot	DATASET @D1points using ($8/(log($2)/log(2))):9 ti CD1 @StyleDP1,\
	DATASET @D2points using ($8/(log($2)/log(2))):9 ti CD2 @StyleDP2,\
	DATASET @D3points using ($8/(log($2)/log(2))):9 ti CD3 @StyleDP3,\
	DATASET @D4points using ($8/(log($2)/log(2))):9 ti CD4 @StyleDP4
@post
set output "fn_vs_entropy_usincome.eps"
replot
! epstopdf "fn_vs_entropy_usincome.eps"


reset

set key top left
set nologscale x
set logscale y
set notitle
set grid
set xrange [0:1]
set yrange [:1]


###  False negative and false positive vs. entropy/log(TCS)       ###
set xlabel "{/Times-Italic entropy/log(tag-cloud size)}"
set ylabel "{/Times-Italic False-positive and false-negative indexes}"

plot	DATASET @D1points using ($8/(log($2)/log(2))):10 ti '' @StyleDP1,\
	DATASET @D2points using ($8/(log($2)/log(2))):10 ti '' @StyleDP2,\
	DATASET @D3points using ($8/(log($2)/log(2))):10 ti '' @StyleDP3,\
	DATASET @D4points using ($8/(log($2)/log(2))):10 ti '' @StyleDP4
	
plot	DATASET @D1points using ($8/(log($2)/log(2))):9 ti CD1 @StyleDP1,\
	DATASET @D2points using ($8/(log($2)/log(2))):9 ti CD2 @StyleDP2,\
	DATASET @D3points using ($8/(log($2)/log(2))):9 ti CD3 @StyleDP3,\
	DATASET @D4points using ($8/(log($2)/log(2))):9 ti CD4 @StyleDP4

set nomultiplot
@post
set output "fnfp_vs_entropy_usincome.eps"
replot
! epstopdf "fnfp_vs_entropy_usincome.eps"


reset

set key top left
set nologscale x
set logscale y
set notitle
set grid
DATA='test_iceberg_orgdata'
###  Time vs. # of dimensions       ###
set xlabel "{/Times-Italic # of dimensions}"
set ylabel "{/Times-Italic Time (seconds)}"

plot	DATA using  1: ($2*0.001) ti 'Original data' @StyleD1,\
	DATA using  1: (0.001*($3+$4)/2.0) ti 'Iceberg' @StyleD2
@post
set output "time_dim_usincome.eps"
replot
! epstopdf "time_dim_usincome.eps"


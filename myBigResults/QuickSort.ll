@.QuickSort_vtable = global [0 x i8*] []
@.QS_vtable = global [4 x i8*] [i8* bitcast ( i32 (i8*,i32)* @QS.Start to i8*),i8* bitcast ( i32 (i8*,i32,i32)* @QS.Sort to i8*),i8* bitcast ( i32 (i8*)* @QS.Print to i8*),i8* bitcast ( i32 (i8*,i32)* @QS.Init to i8*)]
declare i8* @calloc(i32, i32)
declare i32 @printf(i8*, ...)
declare void @exit(i32)

@_cint = constant [4 x i8] c"%d\0a\00"
@_cOOB = constant [15 x i8] c"Out of bounds\0a\00"
@_cNSZ = constant [15 x i8] c"Negative size\0a\00"
define void @print_int(i32 %i) {
    %_str = bitcast [4 x i8]* @_cint to i8*
    call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
    ret void
}

define void @throw_oob() {
    %_str = bitcast [15 x i8]* @_cOOB to i8*
    call i32 (i8*, ...) @printf(i8* %_str)
    call void @exit(i32 1)
    ret void
}

define void @throw_nsz() {
    %_str = bitcast [15 x i8]* @_cNSZ to i8*
    call i32 (i8*, ...) @printf(i8* %_str)
    call void @exit(i32 1)
    ret void
}

define i32 @main() {
	%_0 = call i8* @calloc(i32 1, i32 20)
	%_1 = bitcast i8* %_0 to i8***
	%_2  = getelementptr [4 x i8*], [4 x i8*]* @.QS_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	%_3 = bitcast i8* %_0 to i8***
	%_4 = load i8**, i8*** %_3
	%_5 = getelementptr i8*, i8** %_4, i32 0
	%_6 = load i8*, i8** %_5
	%_7 = bitcast i8* %_6 to i32(i8*, i32)*
	%_8 = call i32 %_7(i8* %_0, i32 10)
	call void (i32) @print_int(i32 %_8)
	ret i32 0
}
define i32 @QS.Start(i8* %this, i32 %.sz) {
	%sz = alloca i32
	store i32  %.sz, i32* %sz

	%aux01 = alloca i32
	
	%_0 = bitcast i8* %this to i8***
	%_1 = load i8**, i8*** %_0
	%_2 = getelementptr i8*, i8** %_1, i32 3
	%_3 = load i8*, i8** %_2
	%_4 = bitcast i8* %_3 to i32(i8*, i32)*
	%_5 = load i32, i32* %sz
	
	%_6 = call i32 %_4(i8* %this, i32 %_5)
	store i32 %_6, i32* %aux01
	%_7 = bitcast i8* %this to i8***
	%_8 = load i8**, i8*** %_7
	%_9 = getelementptr i8*, i8** %_8, i32 2
	%_10 = load i8*, i8** %_9
	%_11 = bitcast i8* %_10 to i32(i8*)*
	%_12 = call i32 %_11(i8* %this)
	store i32 %_12, i32* %aux01
	call void (i32) @print_int(i32 9999)
	%_13 = getelementptr i8, i8* %this, i32 16
	%_14 = bitcast i8* %_13 to i32*

	%_15 = load i32, i32* %_14
	
	%_16 = sub i32 %_15, 1
	store i32 %_16, i32* %aux01
	%_17 = bitcast i8* %this to i8***
	%_18 = load i8**, i8*** %_17
	%_19 = getelementptr i8*, i8** %_18, i32 1
	%_20 = load i8*, i8** %_19
	%_21 = bitcast i8* %_20 to i32(i8*, i32, i32)*
	%_22 = load i32, i32* %aux01
	
	%_23 = call i32 %_21(i8* %this, i32 0, i32 %_22)
	store i32 %_23, i32* %aux01
	%_24 = bitcast i8* %this to i8***
	%_25 = load i8**, i8*** %_24
	%_26 = getelementptr i8*, i8** %_25, i32 2
	%_27 = load i8*, i8** %_26
	%_28 = bitcast i8* %_27 to i32(i8*)*
	%_29 = call i32 %_28(i8* %this)
	store i32 %_29, i32* %aux01
	ret i32 0
}
define i32 @QS.Sort(i8* %this, i32 %.left, i32 %.right) {
	%left = alloca i32
	store i32  %.left, i32* %left
	%right = alloca i32
	store i32  %.right, i32* %right

	%v = alloca i32
	
	%i = alloca i32
	
	%j = alloca i32
	
	%nt = alloca i32
	
	%t = alloca i32
	
	%cont01 = alloca i1
	
	%cont02 = alloca i1
	
	%aux03 = alloca i32
	
	store i32 0, i32* %t
	%_0 = load i32, i32* %left
	
	%_1 = load i32, i32* %right
	
	%_2 = icmp slt i32 %_0, %_1
	br i1 %_2, label %if_then0, label %if_else0
		if_then0:
		%_3 = getelementptr i8, i8* %this, i32 8
		%_4 = bitcast i8* %_3 to i32**

		%_5 = load i32*, i32** %_4
		
		%_6 = load i32, i32* %right
		
		%_7 = load i32, i32* %_5
		%_8 = icmp sge i32 %_6, 0
		%_9 = icmp slt i32 %_6, %_7
		%_10 = and i1 %_8, %_9
		br i1 %_10, label %oob_ok_0, label %oob_err_0
		oob_err_0:
		call void @throw_oob()
		br label %oob_ok_0

		oob_ok_0:
		%_11 = add i32 1, %_6
		%_12 = getelementptr i32, i32* %_5, i32 %_11
		%_13 = load i32, i32* %_12
		store i32 %_13, i32* %v
		%_14 = load i32, i32* %left
		
		%_15 = sub i32 %_14, 1
		store i32 %_15, i32* %i
		%_16 = load i32, i32* %right
		
		store i32 %_16, i32* %j
		store i1 1, i1* %cont01
			br label %loop0
			loop0:
			%_17 = load i1, i1* %cont01
			
			br i1 %_17, label %loop1, label %loop2
			loop1:
			store i1 1, i1* %cont02
				br label %loop3
				loop3:
				%_18 = load i1, i1* %cont02
				
				br i1 %_18, label %loop4, label %loop5
				loop4:
				%_19 = load i32, i32* %i
				
				%_20 = add i32 %_19, 1
				store i32 %_20, i32* %i
				%_21 = getelementptr i8, i8* %this, i32 8
				%_22 = bitcast i8* %_21 to i32**

				%_23 = load i32*, i32** %_22
				
				%_24 = load i32, i32* %i
				
				%_25 = load i32, i32* %_23
				%_26 = icmp sge i32 %_24, 0
				%_27 = icmp slt i32 %_24, %_25
				%_28 = and i1 %_26, %_27
				br i1 %_28, label %oob_ok_1, label %oob_err_1
				oob_err_1:
				call void @throw_oob()
				br label %oob_ok_1

				oob_ok_1:
				%_29 = add i32 1, %_24
				%_30 = getelementptr i32, i32* %_23, i32 %_29
				%_31 = load i32, i32* %_30
				store i32 %_31, i32* %aux03
				%_33 = load i32, i32* %aux03
				
				%_34 = load i32, i32* %v
				
				%_35 = icmp slt i32 %_33, %_34
				%_32 = xor i1 1, %_35
				br i1 %_32, label %if_then1, label %if_else1
					if_then1:
					store i1 0, i1* %cont02
					br label %if_end1
					if_else1:
					store i1 1, i1* %cont02
					br label %if_end1
					if_end1:
				br label %loop3
				loop5:
			store i1 1, i1* %cont02
				br label %loop6
				loop6:
				%_36 = load i1, i1* %cont02
				
				br i1 %_36, label %loop7, label %loop8
				loop7:
				%_37 = load i32, i32* %j
				
				%_38 = sub i32 %_37, 1
				store i32 %_38, i32* %j
				%_39 = getelementptr i8, i8* %this, i32 8
				%_40 = bitcast i8* %_39 to i32**

				%_41 = load i32*, i32** %_40
				
				%_42 = load i32, i32* %j
				
				%_43 = load i32, i32* %_41
				%_44 = icmp sge i32 %_42, 0
				%_45 = icmp slt i32 %_42, %_43
				%_46 = and i1 %_44, %_45
				br i1 %_46, label %oob_ok_2, label %oob_err_2
				oob_err_2:
				call void @throw_oob()
				br label %oob_ok_2

				oob_ok_2:
				%_47 = add i32 1, %_42
				%_48 = getelementptr i32, i32* %_41, i32 %_47
				%_49 = load i32, i32* %_48
				store i32 %_49, i32* %aux03
				%_51 = load i32, i32* %v
				
				%_52 = load i32, i32* %aux03
				
				%_53 = icmp slt i32 %_51, %_52
				%_50 = xor i1 1, %_53
				br i1 %_50, label %if_then2, label %if_else2
					if_then2:
					store i1 0, i1* %cont02
					br label %if_end2
					if_else2:
					store i1 1, i1* %cont02
					br label %if_end2
					if_end2:
				br label %loop6
				loop8:
			%_54 = getelementptr i8, i8* %this, i32 8
			%_55 = bitcast i8* %_54 to i32**

			%_56 = load i32*, i32** %_55
			
			%_57 = load i32, i32* %i
			
			%_58 = load i32, i32* %_56
			%_59 = icmp sge i32 %_57, 0
			%_60 = icmp slt i32 %_57, %_58
			%_61 = and i1 %_59, %_60
			br i1 %_61, label %oob_ok_3, label %oob_err_3
			oob_err_3:
			call void @throw_oob()
			br label %oob_ok_3

			oob_ok_3:
			%_62 = add i32 1, %_57
			%_63 = getelementptr i32, i32* %_56, i32 %_62
			%_64 = load i32, i32* %_63
			store i32 %_64, i32* %t
			%_65 = load i32, i32* %i
			
			%_66 = getelementptr i8, i8* %this, i32 8
			%_67 = bitcast i8* %_66 to i32**

			%_68 = load i32*, i32** %_67
			%_69 = load i32, i32* %_68
			%_70 = icmp sge i32 %_65, 0
			%_71 = icmp slt i32 %_65, %_69
			%_72 = and i1 %_70, %_71
			br i1 %_72, label %oob_ok_4, label %oob_err_4
			oob_err_4:
			call void @throw_oob()
			br label %oob_ok_4

			oob_ok_4:
			%_73 = add i32 1, %_65
			%_74 = getelementptr i32, i32* %_68, i32 %_73
			%_75 = getelementptr i8, i8* %this, i32 8
			%_76 = bitcast i8* %_75 to i32**

			%_77 = load i32*, i32** %_76
			
			%_78 = load i32, i32* %j
			
			%_79 = load i32, i32* %_77
			%_80 = icmp sge i32 %_78, 0
			%_81 = icmp slt i32 %_78, %_79
			%_82 = and i1 %_80, %_81
			br i1 %_82, label %oob_ok_5, label %oob_err_5
			oob_err_5:
			call void @throw_oob()
			br label %oob_ok_5

			oob_ok_5:
			%_83 = add i32 1, %_78
			%_84 = getelementptr i32, i32* %_77, i32 %_83
			%_85 = load i32, i32* %_84
			store i32 %_85, i32* %_74
			%_86 = load i32, i32* %j
			
			%_87 = getelementptr i8, i8* %this, i32 8
			%_88 = bitcast i8* %_87 to i32**

			%_89 = load i32*, i32** %_88
			%_90 = load i32, i32* %_89
			%_91 = icmp sge i32 %_86, 0
			%_92 = icmp slt i32 %_86, %_90
			%_93 = and i1 %_91, %_92
			br i1 %_93, label %oob_ok_6, label %oob_err_6
			oob_err_6:
			call void @throw_oob()
			br label %oob_ok_6

			oob_ok_6:
			%_94 = add i32 1, %_86
			%_95 = getelementptr i32, i32* %_89, i32 %_94
			%_96 = load i32, i32* %t
			
			store i32 %_96, i32* %_95
			%_97 = load i32, i32* %j
			
			%_98 = load i32, i32* %i
			
			%_99 = add i32 %_98, 1
			%_100 = icmp slt i32 %_97, %_99
			br i1 %_100, label %if_then3, label %if_else3
				if_then3:
				store i1 0, i1* %cont01
				br label %if_end3
				if_else3:
				store i1 1, i1* %cont01
				br label %if_end3
				if_end3:
			br label %loop0
			loop2:
		%_101 = load i32, i32* %j
		
		%_102 = getelementptr i8, i8* %this, i32 8
		%_103 = bitcast i8* %_102 to i32**

		%_104 = load i32*, i32** %_103
		%_105 = load i32, i32* %_104
		%_106 = icmp sge i32 %_101, 0
		%_107 = icmp slt i32 %_101, %_105
		%_108 = and i1 %_106, %_107
		br i1 %_108, label %oob_ok_7, label %oob_err_7
		oob_err_7:
		call void @throw_oob()
		br label %oob_ok_7

		oob_ok_7:
		%_109 = add i32 1, %_101
		%_110 = getelementptr i32, i32* %_104, i32 %_109
		%_111 = getelementptr i8, i8* %this, i32 8
		%_112 = bitcast i8* %_111 to i32**

		%_113 = load i32*, i32** %_112
		
		%_114 = load i32, i32* %i
		
		%_115 = load i32, i32* %_113
		%_116 = icmp sge i32 %_114, 0
		%_117 = icmp slt i32 %_114, %_115
		%_118 = and i1 %_116, %_117
		br i1 %_118, label %oob_ok_8, label %oob_err_8
		oob_err_8:
		call void @throw_oob()
		br label %oob_ok_8

		oob_ok_8:
		%_119 = add i32 1, %_114
		%_120 = getelementptr i32, i32* %_113, i32 %_119
		%_121 = load i32, i32* %_120
		store i32 %_121, i32* %_110
		%_122 = load i32, i32* %i
		
		%_123 = getelementptr i8, i8* %this, i32 8
		%_124 = bitcast i8* %_123 to i32**

		%_125 = load i32*, i32** %_124
		%_126 = load i32, i32* %_125
		%_127 = icmp sge i32 %_122, 0
		%_128 = icmp slt i32 %_122, %_126
		%_129 = and i1 %_127, %_128
		br i1 %_129, label %oob_ok_9, label %oob_err_9
		oob_err_9:
		call void @throw_oob()
		br label %oob_ok_9

		oob_ok_9:
		%_130 = add i32 1, %_122
		%_131 = getelementptr i32, i32* %_125, i32 %_130
		%_132 = getelementptr i8, i8* %this, i32 8
		%_133 = bitcast i8* %_132 to i32**

		%_134 = load i32*, i32** %_133
		
		%_135 = load i32, i32* %right
		
		%_136 = load i32, i32* %_134
		%_137 = icmp sge i32 %_135, 0
		%_138 = icmp slt i32 %_135, %_136
		%_139 = and i1 %_137, %_138
		br i1 %_139, label %oob_ok_10, label %oob_err_10
		oob_err_10:
		call void @throw_oob()
		br label %oob_ok_10

		oob_ok_10:
		%_140 = add i32 1, %_135
		%_141 = getelementptr i32, i32* %_134, i32 %_140
		%_142 = load i32, i32* %_141
		store i32 %_142, i32* %_131
		%_143 = load i32, i32* %right
		
		%_144 = getelementptr i8, i8* %this, i32 8
		%_145 = bitcast i8* %_144 to i32**

		%_146 = load i32*, i32** %_145
		%_147 = load i32, i32* %_146
		%_148 = icmp sge i32 %_143, 0
		%_149 = icmp slt i32 %_143, %_147
		%_150 = and i1 %_148, %_149
		br i1 %_150, label %oob_ok_11, label %oob_err_11
		oob_err_11:
		call void @throw_oob()
		br label %oob_ok_11

		oob_ok_11:
		%_151 = add i32 1, %_143
		%_152 = getelementptr i32, i32* %_146, i32 %_151
		%_153 = load i32, i32* %t
		
		store i32 %_153, i32* %_152
		%_154 = bitcast i8* %this to i8***
		%_155 = load i8**, i8*** %_154
		%_156 = getelementptr i8*, i8** %_155, i32 1
		%_157 = load i8*, i8** %_156
		%_158 = bitcast i8* %_157 to i32(i8*, i32, i32)*
		%_159 = load i32, i32* %left
		
		%_160 = load i32, i32* %i
		
		%_161 = sub i32 %_160, 1
		%_162 = call i32 %_158(i8* %this, i32 %_159, i32 %_161)
		store i32 %_162, i32* %nt
		%_163 = bitcast i8* %this to i8***
		%_164 = load i8**, i8*** %_163
		%_165 = getelementptr i8*, i8** %_164, i32 1
		%_166 = load i8*, i8** %_165
		%_167 = bitcast i8* %_166 to i32(i8*, i32, i32)*
		%_168 = load i32, i32* %i
		
		%_169 = add i32 %_168, 1
		%_170 = load i32, i32* %right
		
		%_171 = call i32 %_167(i8* %this, i32 %_169, i32 %_170)
		store i32 %_171, i32* %nt
		br label %if_end0
		if_else0:
		store i32 0, i32* %nt
		br label %if_end0
		if_end0:
	ret i32 0
}
define i32 @QS.Print(i8* %this) {

	%j = alloca i32
	
	store i32 0, i32* %j
		br label %loop9
		loop9:
		%_0 = load i32, i32* %j
		
		%_1 = getelementptr i8, i8* %this, i32 16
		%_2 = bitcast i8* %_1 to i32*

		%_3 = load i32, i32* %_2
		
		%_4 = icmp slt i32 %_0, %_3
		br i1 %_4, label %loop10, label %loop11
		loop10:
		%_5 = getelementptr i8, i8* %this, i32 8
		%_6 = bitcast i8* %_5 to i32**

		%_7 = load i32*, i32** %_6
		
		%_8 = load i32, i32* %j
		
		%_9 = load i32, i32* %_7
		%_10 = icmp sge i32 %_8, 0
		%_11 = icmp slt i32 %_8, %_9
		%_12 = and i1 %_10, %_11
		br i1 %_12, label %oob_ok_12, label %oob_err_12
		oob_err_12:
		call void @throw_oob()
		br label %oob_ok_12

		oob_ok_12:
		%_13 = add i32 1, %_8
		%_14 = getelementptr i32, i32* %_7, i32 %_13
		%_15 = load i32, i32* %_14
		call void (i32) @print_int(i32 %_15)
		%_16 = load i32, i32* %j
		
		%_17 = add i32 %_16, 1
		store i32 %_17, i32* %j
		br label %loop9
		loop11:
	ret i32 0
}
define i32 @QS.Init(i8* %this, i32 %.sz) {
	%sz = alloca i32
	store i32  %.sz, i32* %sz

	%_0 = getelementptr i8, i8* %this, i32 16
	%_1 = bitcast i8* %_0 to i32*

	%_2 = load i32, i32* %sz
	
	store i32 %_2, i32* %_1
	%_3 = getelementptr i8, i8* %this, i32 8
	%_4 = bitcast i8* %_3 to i32**

	%_5 = load i32, i32* %sz
	
	%_6 = add i32 1, %_5
	%_7 = icmp sge i32 %_6, 1
	br i1 %_7, label %nsz_ok_0, label %nsz_err_0
	nsz_err_0:
	call void @throw_nsz()
	br label %nsz_ok_0

	nsz_ok_0:
	%_8 = call i8* @calloc(i32 %_6, i32 4)
	%_9 = bitcast i8* %_8 to i32*
	store i32 %_5, i32* %_9
	store i32* %_9, i32** %_4
	%_10 = getelementptr i8, i8* %this, i32 8
	%_11 = bitcast i8* %_10 to i32**

	%_12 = load i32*, i32** %_11
	%_13 = load i32, i32* %_12
	%_14 = icmp sge i32 0, 0
	%_15 = icmp slt i32 0, %_13
	%_16 = and i1 %_14, %_15
	br i1 %_16, label %oob_ok_13, label %oob_err_13
	oob_err_13:
	call void @throw_oob()
	br label %oob_ok_13

	oob_ok_13:
	%_17 = add i32 1, 0
	%_18 = getelementptr i32, i32* %_12, i32 %_17
	store i32 20, i32* %_18
	%_19 = getelementptr i8, i8* %this, i32 8
	%_20 = bitcast i8* %_19 to i32**

	%_21 = load i32*, i32** %_20
	%_22 = load i32, i32* %_21
	%_23 = icmp sge i32 1, 0
	%_24 = icmp slt i32 1, %_22
	%_25 = and i1 %_23, %_24
	br i1 %_25, label %oob_ok_14, label %oob_err_14
	oob_err_14:
	call void @throw_oob()
	br label %oob_ok_14

	oob_ok_14:
	%_26 = add i32 1, 1
	%_27 = getelementptr i32, i32* %_21, i32 %_26
	store i32 7, i32* %_27
	%_28 = getelementptr i8, i8* %this, i32 8
	%_29 = bitcast i8* %_28 to i32**

	%_30 = load i32*, i32** %_29
	%_31 = load i32, i32* %_30
	%_32 = icmp sge i32 2, 0
	%_33 = icmp slt i32 2, %_31
	%_34 = and i1 %_32, %_33
	br i1 %_34, label %oob_ok_15, label %oob_err_15
	oob_err_15:
	call void @throw_oob()
	br label %oob_ok_15

	oob_ok_15:
	%_35 = add i32 1, 2
	%_36 = getelementptr i32, i32* %_30, i32 %_35
	store i32 12, i32* %_36
	%_37 = getelementptr i8, i8* %this, i32 8
	%_38 = bitcast i8* %_37 to i32**

	%_39 = load i32*, i32** %_38
	%_40 = load i32, i32* %_39
	%_41 = icmp sge i32 3, 0
	%_42 = icmp slt i32 3, %_40
	%_43 = and i1 %_41, %_42
	br i1 %_43, label %oob_ok_16, label %oob_err_16
	oob_err_16:
	call void @throw_oob()
	br label %oob_ok_16

	oob_ok_16:
	%_44 = add i32 1, 3
	%_45 = getelementptr i32, i32* %_39, i32 %_44
	store i32 18, i32* %_45
	%_46 = getelementptr i8, i8* %this, i32 8
	%_47 = bitcast i8* %_46 to i32**

	%_48 = load i32*, i32** %_47
	%_49 = load i32, i32* %_48
	%_50 = icmp sge i32 4, 0
	%_51 = icmp slt i32 4, %_49
	%_52 = and i1 %_50, %_51
	br i1 %_52, label %oob_ok_17, label %oob_err_17
	oob_err_17:
	call void @throw_oob()
	br label %oob_ok_17

	oob_ok_17:
	%_53 = add i32 1, 4
	%_54 = getelementptr i32, i32* %_48, i32 %_53
	store i32 2, i32* %_54
	%_55 = getelementptr i8, i8* %this, i32 8
	%_56 = bitcast i8* %_55 to i32**

	%_57 = load i32*, i32** %_56
	%_58 = load i32, i32* %_57
	%_59 = icmp sge i32 5, 0
	%_60 = icmp slt i32 5, %_58
	%_61 = and i1 %_59, %_60
	br i1 %_61, label %oob_ok_18, label %oob_err_18
	oob_err_18:
	call void @throw_oob()
	br label %oob_ok_18

	oob_ok_18:
	%_62 = add i32 1, 5
	%_63 = getelementptr i32, i32* %_57, i32 %_62
	store i32 11, i32* %_63
	%_64 = getelementptr i8, i8* %this, i32 8
	%_65 = bitcast i8* %_64 to i32**

	%_66 = load i32*, i32** %_65
	%_67 = load i32, i32* %_66
	%_68 = icmp sge i32 6, 0
	%_69 = icmp slt i32 6, %_67
	%_70 = and i1 %_68, %_69
	br i1 %_70, label %oob_ok_19, label %oob_err_19
	oob_err_19:
	call void @throw_oob()
	br label %oob_ok_19

	oob_ok_19:
	%_71 = add i32 1, 6
	%_72 = getelementptr i32, i32* %_66, i32 %_71
	store i32 6, i32* %_72
	%_73 = getelementptr i8, i8* %this, i32 8
	%_74 = bitcast i8* %_73 to i32**

	%_75 = load i32*, i32** %_74
	%_76 = load i32, i32* %_75
	%_77 = icmp sge i32 7, 0
	%_78 = icmp slt i32 7, %_76
	%_79 = and i1 %_77, %_78
	br i1 %_79, label %oob_ok_20, label %oob_err_20
	oob_err_20:
	call void @throw_oob()
	br label %oob_ok_20

	oob_ok_20:
	%_80 = add i32 1, 7
	%_81 = getelementptr i32, i32* %_75, i32 %_80
	store i32 9, i32* %_81
	%_82 = getelementptr i8, i8* %this, i32 8
	%_83 = bitcast i8* %_82 to i32**

	%_84 = load i32*, i32** %_83
	%_85 = load i32, i32* %_84
	%_86 = icmp sge i32 8, 0
	%_87 = icmp slt i32 8, %_85
	%_88 = and i1 %_86, %_87
	br i1 %_88, label %oob_ok_21, label %oob_err_21
	oob_err_21:
	call void @throw_oob()
	br label %oob_ok_21

	oob_ok_21:
	%_89 = add i32 1, 8
	%_90 = getelementptr i32, i32* %_84, i32 %_89
	store i32 19, i32* %_90
	%_91 = getelementptr i8, i8* %this, i32 8
	%_92 = bitcast i8* %_91 to i32**

	%_93 = load i32*, i32** %_92
	%_94 = load i32, i32* %_93
	%_95 = icmp sge i32 9, 0
	%_96 = icmp slt i32 9, %_94
	%_97 = and i1 %_95, %_96
	br i1 %_97, label %oob_ok_22, label %oob_err_22
	oob_err_22:
	call void @throw_oob()
	br label %oob_ok_22

	oob_ok_22:
	%_98 = add i32 1, 9
	%_99 = getelementptr i32, i32* %_93, i32 %_98
	store i32 5, i32* %_99
	ret i32 0
}

@.BinaryTree_vtable = global [0 x i8*] []
@.BT_vtable = global [1 x i8*] [i8* bitcast ( i32 (i8*)* @BT.Start to i8*)]
@.Tree_vtable = global [20 x i8*] [i8* bitcast ( i1 (i8*,i32)* @Tree.Init to i8*),i8* bitcast ( i1 (i8*,i8*)* @Tree.SetRight to i8*),i8* bitcast ( i1 (i8*,i8*)* @Tree.SetLeft to i8*),i8* bitcast ( i8* (i8*)* @Tree.GetRight to i8*),i8* bitcast ( i8* (i8*)* @Tree.GetLeft to i8*),i8* bitcast ( i32 (i8*)* @Tree.GetKey to i8*),i8* bitcast ( i1 (i8*,i32)* @Tree.SetKey to i8*),i8* bitcast ( i1 (i8*)* @Tree.GetHas_Right to i8*),i8* bitcast ( i1 (i8*)* @Tree.GetHas_Left to i8*),i8* bitcast ( i1 (i8*,i1)* @Tree.SetHas_Left to i8*),i8* bitcast ( i1 (i8*,i1)* @Tree.SetHas_Right to i8*),i8* bitcast ( i1 (i8*,i32,i32)* @Tree.Compare to i8*),i8* bitcast ( i1 (i8*,i32)* @Tree.Insert to i8*),i8* bitcast ( i1 (i8*,i32)* @Tree.Delete to i8*),i8* bitcast ( i1 (i8*,i8*,i8*)* @Tree.Remove to i8*),i8* bitcast ( i1 (i8*,i8*,i8*)* @Tree.RemoveRight to i8*),i8* bitcast ( i1 (i8*,i8*,i8*)* @Tree.RemoveLeft to i8*),i8* bitcast ( i32 (i8*,i32)* @Tree.Search to i8*),i8* bitcast ( i1 (i8*)* @Tree.Print to i8*),i8* bitcast ( i1 (i8*,i8*)* @Tree.RecPrint to i8*)]
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
	%_0 = call i8* @calloc(i32 1, i32 8)
	%_1 = bitcast i8* %_0 to i8***
	%_2  = getelementptr [1 x i8*], [1 x i8*]* @.BT_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	%_3 = bitcast i8* %_0 to i8***
	%_4 = load i8**, i8*** %_3
	%_5 = getelementptr i8*, i8** %_4, i32 0
	%_6 = load i8*, i8** %_5
	%_7 = bitcast i8* %_6 to i32(i8*)*
	%_8 = call i32 %_7(i8* %_0)
	call void (i32) @print_int(i32 %_8)
	ret i32 0
}
define i32 @BT.Start(i8* %this) {

	%root = alloca i8*
	
	%ntb = alloca i1
	
	%nti = alloca i32
	
	%_0 = call i8* @calloc(i32 1, i32 38)
	%_1 = bitcast i8* %_0 to i8***
	%_2  = getelementptr [20 x i8*], [20 x i8*]* @.Tree_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	store i8* %_0, i8** %root
	%_3 = load i8*, i8** %root
	
	%_4 = bitcast i8* %_3 to i8***
	%_5 = load i8**, i8*** %_4
	%_6 = getelementptr i8*, i8** %_5, i32 0
	%_7 = load i8*, i8** %_6
	%_8 = bitcast i8* %_7 to i1(i8*, i32)*
	%_9 = call i1 %_8(i8* %_3, i32 16)
	store i1 %_9, i1* %ntb
	%_10 = load i8*, i8** %root
	
	%_11 = bitcast i8* %_10 to i8***
	%_12 = load i8**, i8*** %_11
	%_13 = getelementptr i8*, i8** %_12, i32 18
	%_14 = load i8*, i8** %_13
	%_15 = bitcast i8* %_14 to i1(i8*)*
	%_16 = call i1 %_15(i8* %_10)
	store i1 %_16, i1* %ntb
	call void (i32) @print_int(i32 100000000)
	%_17 = load i8*, i8** %root
	
	%_18 = bitcast i8* %_17 to i8***
	%_19 = load i8**, i8*** %_18
	%_20 = getelementptr i8*, i8** %_19, i32 12
	%_21 = load i8*, i8** %_20
	%_22 = bitcast i8* %_21 to i1(i8*, i32)*
	%_23 = call i1 %_22(i8* %_17, i32 8)
	store i1 %_23, i1* %ntb
	%_24 = load i8*, i8** %root
	
	%_25 = bitcast i8* %_24 to i8***
	%_26 = load i8**, i8*** %_25
	%_27 = getelementptr i8*, i8** %_26, i32 18
	%_28 = load i8*, i8** %_27
	%_29 = bitcast i8* %_28 to i1(i8*)*
	%_30 = call i1 %_29(i8* %_24)
	store i1 %_30, i1* %ntb
	%_31 = load i8*, i8** %root
	
	%_32 = bitcast i8* %_31 to i8***
	%_33 = load i8**, i8*** %_32
	%_34 = getelementptr i8*, i8** %_33, i32 12
	%_35 = load i8*, i8** %_34
	%_36 = bitcast i8* %_35 to i1(i8*, i32)*
	%_37 = call i1 %_36(i8* %_31, i32 24)
	store i1 %_37, i1* %ntb
	%_38 = load i8*, i8** %root
	
	%_39 = bitcast i8* %_38 to i8***
	%_40 = load i8**, i8*** %_39
	%_41 = getelementptr i8*, i8** %_40, i32 12
	%_42 = load i8*, i8** %_41
	%_43 = bitcast i8* %_42 to i1(i8*, i32)*
	%_44 = call i1 %_43(i8* %_38, i32 4)
	store i1 %_44, i1* %ntb
	%_45 = load i8*, i8** %root
	
	%_46 = bitcast i8* %_45 to i8***
	%_47 = load i8**, i8*** %_46
	%_48 = getelementptr i8*, i8** %_47, i32 12
	%_49 = load i8*, i8** %_48
	%_50 = bitcast i8* %_49 to i1(i8*, i32)*
	%_51 = call i1 %_50(i8* %_45, i32 12)
	store i1 %_51, i1* %ntb
	%_52 = load i8*, i8** %root
	
	%_53 = bitcast i8* %_52 to i8***
	%_54 = load i8**, i8*** %_53
	%_55 = getelementptr i8*, i8** %_54, i32 12
	%_56 = load i8*, i8** %_55
	%_57 = bitcast i8* %_56 to i1(i8*, i32)*
	%_58 = call i1 %_57(i8* %_52, i32 20)
	store i1 %_58, i1* %ntb
	%_59 = load i8*, i8** %root
	
	%_60 = bitcast i8* %_59 to i8***
	%_61 = load i8**, i8*** %_60
	%_62 = getelementptr i8*, i8** %_61, i32 12
	%_63 = load i8*, i8** %_62
	%_64 = bitcast i8* %_63 to i1(i8*, i32)*
	%_65 = call i1 %_64(i8* %_59, i32 28)
	store i1 %_65, i1* %ntb
	%_66 = load i8*, i8** %root
	
	%_67 = bitcast i8* %_66 to i8***
	%_68 = load i8**, i8*** %_67
	%_69 = getelementptr i8*, i8** %_68, i32 12
	%_70 = load i8*, i8** %_69
	%_71 = bitcast i8* %_70 to i1(i8*, i32)*
	%_72 = call i1 %_71(i8* %_66, i32 14)
	store i1 %_72, i1* %ntb
	%_73 = load i8*, i8** %root
	
	%_74 = bitcast i8* %_73 to i8***
	%_75 = load i8**, i8*** %_74
	%_76 = getelementptr i8*, i8** %_75, i32 18
	%_77 = load i8*, i8** %_76
	%_78 = bitcast i8* %_77 to i1(i8*)*
	%_79 = call i1 %_78(i8* %_73)
	store i1 %_79, i1* %ntb
	%_80 = load i8*, i8** %root
	
	%_81 = bitcast i8* %_80 to i8***
	%_82 = load i8**, i8*** %_81
	%_83 = getelementptr i8*, i8** %_82, i32 17
	%_84 = load i8*, i8** %_83
	%_85 = bitcast i8* %_84 to i32(i8*, i32)*
	%_86 = call i32 %_85(i8* %_80, i32 24)
	call void (i32) @print_int(i32 %_86)
	%_87 = load i8*, i8** %root
	
	%_88 = bitcast i8* %_87 to i8***
	%_89 = load i8**, i8*** %_88
	%_90 = getelementptr i8*, i8** %_89, i32 17
	%_91 = load i8*, i8** %_90
	%_92 = bitcast i8* %_91 to i32(i8*, i32)*
	%_93 = call i32 %_92(i8* %_87, i32 12)
	call void (i32) @print_int(i32 %_93)
	%_94 = load i8*, i8** %root
	
	%_95 = bitcast i8* %_94 to i8***
	%_96 = load i8**, i8*** %_95
	%_97 = getelementptr i8*, i8** %_96, i32 17
	%_98 = load i8*, i8** %_97
	%_99 = bitcast i8* %_98 to i32(i8*, i32)*
	%_100 = call i32 %_99(i8* %_94, i32 16)
	call void (i32) @print_int(i32 %_100)
	%_101 = load i8*, i8** %root
	
	%_102 = bitcast i8* %_101 to i8***
	%_103 = load i8**, i8*** %_102
	%_104 = getelementptr i8*, i8** %_103, i32 17
	%_105 = load i8*, i8** %_104
	%_106 = bitcast i8* %_105 to i32(i8*, i32)*
	%_107 = call i32 %_106(i8* %_101, i32 50)
	call void (i32) @print_int(i32 %_107)
	%_108 = load i8*, i8** %root
	
	%_109 = bitcast i8* %_108 to i8***
	%_110 = load i8**, i8*** %_109
	%_111 = getelementptr i8*, i8** %_110, i32 17
	%_112 = load i8*, i8** %_111
	%_113 = bitcast i8* %_112 to i32(i8*, i32)*
	%_114 = call i32 %_113(i8* %_108, i32 12)
	call void (i32) @print_int(i32 %_114)
	%_115 = load i8*, i8** %root
	
	%_116 = bitcast i8* %_115 to i8***
	%_117 = load i8**, i8*** %_116
	%_118 = getelementptr i8*, i8** %_117, i32 13
	%_119 = load i8*, i8** %_118
	%_120 = bitcast i8* %_119 to i1(i8*, i32)*
	%_121 = call i1 %_120(i8* %_115, i32 12)
	store i1 %_121, i1* %ntb
	%_122 = load i8*, i8** %root
	
	%_123 = bitcast i8* %_122 to i8***
	%_124 = load i8**, i8*** %_123
	%_125 = getelementptr i8*, i8** %_124, i32 18
	%_126 = load i8*, i8** %_125
	%_127 = bitcast i8* %_126 to i1(i8*)*
	%_128 = call i1 %_127(i8* %_122)
	store i1 %_128, i1* %ntb
	%_129 = load i8*, i8** %root
	
	%_130 = bitcast i8* %_129 to i8***
	%_131 = load i8**, i8*** %_130
	%_132 = getelementptr i8*, i8** %_131, i32 17
	%_133 = load i8*, i8** %_132
	%_134 = bitcast i8* %_133 to i32(i8*, i32)*
	%_135 = call i32 %_134(i8* %_129, i32 12)
	call void (i32) @print_int(i32 %_135)
	ret i32 0
}
define i1 @Tree.Init(i8* %this, i32 %.v_key) {
	%v_key = alloca i32
	store i32  %.v_key, i32* %v_key

	%_0 = getelementptr i8, i8* %this, i32 24
	%_1 = bitcast i8* %_0 to i32*

	%_2 = load i32, i32* %v_key
	
	store i32 %_2, i32* %_1
	%_3 = getelementptr 
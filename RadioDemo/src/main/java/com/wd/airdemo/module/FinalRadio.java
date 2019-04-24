package com.wd.airdemo.module;

public class FinalRadio {
    public static final int C_NEXT_CHANNEL					= 0; // 当前波段分号，1-N个台， 向下跳台（不用搜索，只在已经存放的列表中进行循环跳转
    public static final int C_PREV_CHANNEL					= 1; // 当前波段分号，1-N个台， 向上跳台（不用搜索，只在已经存放的列表中进行循环跳转
    public static final int C_FREQ_UP						= 3; // 当前波段，按频率点步进，减步进
    public static final int C_FREQ_DOWN						= 4; // 当前波段，按频率点步进，加步进
    public static final int C_SEEK_UP						= 5; // 当前波段，在当前频率下，向上搜台
    public static final int C_SEEK_DOWN						= 6; // 当前波段，在当前频率下，向下搜台
    public static final int C_SELECT_CHANNEL				= 7; // 在当前波段分号，选中1-6其中一个台
    public static final int C_SAVE_CHANNEL					= 8; // 将当前播放的频率保存到指定的台号
    public static final int C_SCAN							= 9; // 搜索当前波段，搜到信号后播放3s，然后继续搜索下一台，并保存
    public static final int C_SAVE							= 10;
    // args:C_BAND, new int[]{value or BAND_XXX}, null, null
    public static final int C_BAND							= 11;
    public static final int C_AREA							= 12;
    // args:C_FREQ, new int[]{方式[FREQ_xxx], value}, null, null
    public static final int C_FREQ							= 13; //直接跳到指定频率进行播放
    // args:C_SENSITY, new int[]{AM 0/FM 1, value >= 0/op[-1PLUS,-2MINUS]}, null, null
    public static final int C_SENSITY						= 14;
    public static final int C_AUTO_SENSITY					= 15;
    public static final int C_RDS_ENABLE					= 16;
    // PARAM:new int[]{1 on/0 off/2 switch}
    public static final int C_STERO							= 17;
    // PARAM:new int[]{1 on/0 off/2 switch}
    public static final int C_LOC							= 18;
    // PARAM:new int[]{1 on/0 off/2 switch}
    public static final int C_RDS_TA_ENABLE					= 19;
    // PARAM:new int[]{1 on/0 off/2 switch}
    public static final int C_RDS_AF_ENABLE					= 20;
    // PARAM:new int[]{1 on/0 off/2 switch}
    public static final int C_RDS_PTY_ENABLE				= 21;
    // PARAM:new int[]{0 off/1 on/2 switch}
    public static final int C_SEARCH						= 22; // 在当前波段，进入搜索模式，并保存
    public static final int C_SORT_TYPE						= 23;
    public static final int C_AIR_LINE						= 24;	// 天线常开
    public static final int C_POWER							= 25;	// 收音机开关控制
    // PARAM: new int[]{TYPE, info} new String[]{value}
    // type:0:收音机频率, ints[1]:当前的收音机频率8750
    // type:1 收音机信息汇总，MCU新定义
    // 新定义分析：:
    // ints[1]:当前状态
//		  Bit0:Search Status //收索
//		      1:Searching
//		      0:Normal
//		  Bit1:Scan Status // 浏览
//		      1:Scaning
//		      0:Normal
//		  Bit2:Stereo Switch //立体声开关
//		      1:Stereo On
//		      0:Mono On
//		  Bit3:Stereo Status//立体声点灯，界面st亮了
//		      1:ST flag on(Not Mono On)
//		      0:ST flag off
//		  Bit4:LOC/DX//远近程
//		      1:DX
//		      0:LOC
//		  Bit5:reserve
//		  Bit6:reserve
//		  Bit7:reserve
//		ints[2]:当前波段(FM:0.1.2,AM:3.4)
//		ints[3]:当前频道(0.1-6)(0非预置)
//		ints[4]:当前频率H(单位Khz)87.50=87500K
//		ints[5]:当前频率M(单位Khz)522=522K
//		ints[6]:当前频率L(单位Khz)
    public static final int C_RADIO_INFO					= 26;	// 收音机信息转发给服务


	/**
	 *
	 *           update
	 *
	 */

	// PARAM: new int[]{band}
    // eg: band等于BAND_FM_INDEX_BEGIN+0,代表FM1,类推
    public static final int U_BAND							= 0;	// 当前波段
    // PARAM: new int[]{freq}  TIP:<5000代表AM频率,否则为FM频率
    // eg 1762:1762kHz   10750:107.50MHz
    public static final int U_FREQ							= 1;	// 当前频率
    // PARAM: new int[]{area}
    public static final int U_AREA							= 2;
    public static final int U_CHANNEL						= 3;    //当前台信息
	// new int[]{currentFreq, freq0, freq1, ... freqN}
	// currentFreq 表示当前的频率信息
	// freq0 ... freqN 表示第0~N台频率
    public static final int U_CHANNEL_FREQ					= 4;    // 更新搜台的索引列表信息
    public static final int U_CURRENT_FREQ					= 5; 	// 当前播放的频率
	// 0：无， 1， 向上搜台， 2，向下搜台
    public static final int U_SEEK_STATE					= 6;	// 上下搜台状态
//    public static final int U_RDS_TA						= 7;
//    public static final int U_RDS_TP						= 8;
//    public static final int U_RDS_TA_ENABLE					= 9;
//    public static final int U_RDS_PI_SEEK					= 10;
//    public static final int U_RDS_TA_SEEK					= 11;
//    public static final int U_RDS_PTY_SEEK					= 12;
//    public static final int U_RDS_TEXT						= 13;
//    public static final int U_RDS_CHANNEL_TEXT				= 14;
//    public static final int U_RDS_ENABLE					= 15;
//    // 频率信息:最小,最大频率,频率步长,频率步数
//    public static final int U_EXTRA_FREQ_INFO				= 16;
//    public static final int U_SENSITY_AM					= 17;
//    public static final int U_SENSITY_FM					= 18;
//    public static final int U_AUTO_SENSITY					= 19;
    public static final int U_SCAN							= 20;   // 浏览当前波段频率信息（AS）
    public static final int U_STEREO						= 21;
    public static final int U_SEARCH_STATE					= 22;   // 当前搜台状态信息
    public static final int U_LOC							= 23;	// 本地/远程
    public static final int U_SORT_TYPE						= 24;	// 排序类型
    public static final int U_AIR_LINE						= 25;	// 天线常开
    public static final int U_PS_TEXT						= 26;	//
    public static final int U_POWER							= 27;   // 当前收音机开关状态
    public static final int U_INNER_CMD						= 28;	// 内置收音机控制命令
    // Grade M.
//    public static final int U_CNT_MAX						= 100;
    public static final int U_CNT_MAX						= U_INNER_CMD + 1;

    public static final int CHANNEL_AM_INDEX_BEGIN			= 0x00000;	// 暂时AM 12个 CHANNEL
    public static final int CHANNEL_AM_INDEX_END			= 0x10000;	// 暂时FM 18个 CHANNEL
    public static final int CHANNEL_FM_INDEX_BEGIN			= 0x10000;
    public static final int CHANNEL_FM_INDEX_END			= 0x20000;

    public static final int BAND_AM_INDEX_BEGIN				= 0x00000;	// 暂时AM 2个 BAND,每个BAND 6个 CHANNEL
    public static final int BAND_AM_INDEX_END				= 0x10000;
    public static final int BAND_FM_INDEX_BEGIN				= 0x10000;	// 暂时FM 3个 BAND,每个BAND 6个 CHANNEL
    public static final int BAND_FM_INDEX_END				= 0x20000;

    public static final int BAND_SWITCH_ALL					= -1;
    public static final int BAND_SWITCH_AM					= -2;
    public static final int BAND_SWITCH_FM					= -3;

    public static final int AREA_USA						= 0;
    public static final int AREA_LATIN						= 1;
    public static final int AREA_EUROPE						= 2;
    public static final int AREA_CHINA						= 2;	// 中国和欧洲一样
    public static final int AREA_OIRT						= 3;
    public static final int AREA_JAPAN						= 4;

    public static final int FREQ_BY_STEP					= 0;	// 按步数
    public static final int FREQ_DIRECT						= 1;	// 直接设值
    public static final int FREQ_BY_RATIO					= 2;	// 按在波段中最小最大频率的比例 [0,0xFFFF]
    public static final int FREQ_DIRECT_NEW					= 3;	// 直接设值 16/5/29

    public static final int SEARCH_STATE_NONE				= 0;
    public static final int SEARCH_STATE_AUTO				= 1;
    public static final int SEARCH_STATE_FORE				= 2;
    public static final int SEARCH_STATE_BACK				= 3;

    public static final String SERVICE_MS_ACTION			= "com.syu.ms.radio";

    public static final int SORT_TYPE_BY_SIGNAL_HL			= 0;	// 信号强到弱
    public static final int SORT_TYPE_BY_FREQ_LH			= 1;	// 频率低到高

}

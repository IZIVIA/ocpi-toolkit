package com.izivia.ocpi.toolkit.modules.locations.domain

/**
 * The socket or plug standard of the charging point.
 */
enum class ConnectorType {
    /**
     * 	The connector type is CHAdeMO, DC
     */
    CHADEMO,

    /**
     * 	Standard/Domestic household, type "A", NEMA 1-15, 2 pins
     */
    DOMESTIC_A,

    /**
     * 	Standard/Domestic household, type "B", NEMA 5-15, 3 pins
     */
    DOMESTIC_B,

    /**
     * 	Standard/Domestic household, type "C", CEE 7/17, 2 pins
     */
    DOMESTIC_C,

    /**
     * 	Standard/Domestic household, type "D", 3 pin
     */
    DOMESTIC_D,

    /**
     * 	Standard/Domestic household, type "E", CEE 7/5 3 pins
     */
    DOMESTIC_E,

    /**
     * 	Standard/Domestic household, type "F", CEE 7/4, Schuko, 3 pins
     */
    DOMESTIC_F,

    /**
     * 	Standard/Domestic household, type "G", BS 1363, Commonwealth, 3 pins
     */
    DOMESTIC_G,

    /**
     * 	Standard/Domestic household, type "H", SI-32, 3 pins
     */
    DOMESTIC_H,

    /**
     * 	Standard/Domestic household, type "I", AS 3112, 3 pins
     */
    DOMESTIC_I,

    /**
     * 	Standard/Domestic household, type "J", SEV 1011, 3 pins
     */
    DOMESTIC_J,

    /**
     * 	Standard/Domestic household, type "K", DS 60884-2-D1, 3 pins
     */
    DOMESTIC_K,

    /**
     * 	Standard/Domestic household, type "L", CEI 23-16-VII, 3 pins
     */
    DOMESTIC_L,

    /**
     * 	IEC 60309-2 Industrial Connector single phase 16 Amperes (usually blue)
     */
    IEC_60309_2_single_16,

    /**
     * 	IEC 60309-2 Industrial Connector three phase 16 Amperes (usually red)
     */
    IEC_60309_2_three_16,

    /**
     * 	IEC 60309-2 Industrial Connector three phase 32 Amperes (usually red)
     */
    IEC_60309_2_three_32,

    /**
     * 	IEC 60309-2 Industrial Connector three phase 64 Amperes (usually red)
     */
    IEC_60309_2_three_64,

    /**
     * 	IEC 62196 Type 1 "SAE J1772"
     */
    IEC_62196_T1,

    /**
     * 	Combo Type 1 based, DC
     */
    IEC_62196_T1_COMBO,

    /**
     * 	IEC 62196 Type 2 "Mennekes"
     */
    IEC_62196_T2,

    /**
     * 	Combo Type 2 based, DC
     */
    IEC_62196_T2_COMBO,

    /**
     * 	IEC 62196 Type 3A
     */
    IEC_62196_T3A,

    /**
     * 	IEC 62196 Type 3C "Scame"
     */
    IEC_62196_T3C,

    /**
     * 	Tesla Connector "Roadster"-type (round, 4 pin)
     */
    TESLA_R,

    /**
     * 	Tesla Connector "Model-S"-type (oval, 5 pin)
     */
    TESLA_S
}
package com.izivia.ocpi.toolkit.modules.locations.domain

/**
 * The socket or plug standard of the charging point.
 */
enum class ConnectorType {
    /**
     * The connector type is CHAdeMO, DC
     */
    CHADEMO,

    /**
     * The ChaoJi connector. The new generation charging connector, harmonized between CHAdeMO and GB/T. DC.
     */
    CHAOJI,

    /**
     * Standard/Domestic household, type "A", NEMA 1-15, 2 pins
     */
    DOMESTIC_A,

    /**
     * Standard/Domestic household, type "B", NEMA 5-15, 3 pins
     */
    DOMESTIC_B,

    /**
     * Standard/Domestic household, type "C", CEE 7/17, 2 pins
     */
    DOMESTIC_C,

    /**
     * Standard/Domestic household, type "D", 3 pin
     */
    DOMESTIC_D,

    /**
     * Standard/Domestic household, type "E", CEE 7/5 3 pins
     */
    DOMESTIC_E,

    /**
     * Standard/Domestic household, type "F", CEE 7/4, Schuko, 3 pins
     */
    DOMESTIC_F,

    /**
     * Standard/Domestic household, type "G", BS 1363, Commonwealth, 3 pins
     */
    DOMESTIC_G,

    /**
     * Standard/Domestic household, type "H", SI-32, 3 pins
     */
    DOMESTIC_H,

    /**
     * Standard/Domestic household, type "I", AS 3112, 3 pins
     */
    DOMESTIC_I,

    /**
     * Standard/Domestic household, type "J", SEV 1011, 3 pins
     */
    DOMESTIC_J,

    /**
     * Standard/Domestic household, type "K", DS 60884-2-D1, 3 pins
     */
    DOMESTIC_K,

    /**
     * Standard/Domestic household, type "L", CEI 23-16-VII, 3 pins
     */
    DOMESTIC_L,

    /**
     * Standard/Domestic household, type "M", BS 546, 3 pins
     */
    DOMESTIC_M,

    /**
     * Standard/Domestic household, type "N", NBR 14136, 3 pins
     */
    DOMESTIC_N,

    /**
     * Standard/Domestic household, type "O", TIS 166-2549, 3 pins
     */
    DOMESTIC_O,

    /**
     * Guobiao GB/T 20234.2 AC socket/connector
     */
    GBT_AC,

    /**
     * Guobiao GB/T 20234.3 DC connector
     */
    GBT_DC,

    /**
     * IEC 60309-2 Industrial Connector single phase 16 amperes (usually blue)
     */
    IEC_60309_2_single_16,

    /**
     * IEC 60309-2 Industrial Connector three phases 16 amperes (usually red)
     */
    IEC_60309_2_three_16,

    /**
     * IEC 60309-2 Industrial Connector three phases 32 amperes (usually red)
     */
    IEC_60309_2_three_32,

    /**
     * IEC 60309-2 Industrial Connector three phases 64 amperes (usually red)
     */
    IEC_60309_2_three_64,

    /**
     * IEC 62196 Type 1 "SAE J1772"
     */
    IEC_62196_T1,

    /**
     * Combo Type 1 based, DC
     */
    IEC_62196_T1_COMBO,

    /**
     * IEC 62196 Type 2 "Mennekes"
     */
    IEC_62196_T2,

    /**
     * Combo Type 2 based, DC
     */
    IEC_62196_T2_COMBO,

    /**
     * IEC 62196 Type 3A
     */
    IEC_62196_T3A,

    /**
     * IEC 62196 Type 3C "Scame"
     */
    IEC_62196_T3C,

    /**
     * NEMA 5-20, 3 pins
     */
    NEMA_5_20,

    /**
     * NEMA 6-30, 3 pins
     */
    NEMA_6_30,

    /**
     * NEMA 6-50, 3 pins
     */
    NEMA_6_50,

    /**
     * NEMA 10-30, 3 pins
     */
    NEMA_10_30,

    /**
     * NEMA 10-50, 3 pins
     */
    NEMA_10_50,

    /**
     * NEMA 14-30, 3 pins, rating of 30 A
     */
    NEMA_14_30,

    /**
     * NEMA 14-50, 3 pins, rating of 50 A
     */
    NEMA_14_50,

    /**
     * On-board Bottom-up-Pantograph typically for bus charging
     */
    PANTOGRAPH_BOTTOM_UP,

    /**
     * Off-board Top-down-Pantograph typically for bus charging
     */
    PANTOGRAPH_TOP_DOWN,

    /**
     * Tesla Connector "Roadster"-type (round, 4 pin)
     */
    TESLA_R,

    /**
     * Tesla Connector "Model-S"-type (oval, 5 pin)
     */
    TESLA_S,
}

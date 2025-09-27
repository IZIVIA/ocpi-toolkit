package com.izivia.ocpi.toolkit.mappers.data

object JsonMappingData {
    fun connector(tariffIds: String = "[\"tariffId\"]", connectorType: String = "\"IEC_62196_T2\"") = """
        {
          "id" : "id",
          "standard" : $connectorType,
          "format" : "SOCKET",
          "power_type" : "AC_3_PHASE",
          "max_voltage" : 220,
          "max_amperage" : 16,
          "max_electric_power" : 7000,
          "tariff_ids" : $tariffIds,
          "terms_and_conditions" : "termsAndConditions",
          "last_updated" : "2025-01-02T13:45:59.708Z"
        }
    """.trimIndent()

    fun location(
        capabilities: String = "\"RESERVABLE\"",
        facilities: String = "\"BIKE_SHARING\"",
        parkingRestrictions: String = "\"CUSTOMERS\"",
        imageCategory: String = "\"LOCATION\"",
        parkingType: String = "\"PARKING_LOT\"",
        connectorType: String = "\"IEC_62196_T2\"",
    ) = """
        {
          "country_code" : "FR",
          "party_id" : "ABC",
          "id" : "id",
          "publish" : true,
          "publish_allowed_to" : [ {
            "uid" : "uid",
            "type" : "RFID",
            "visual_number" : "visualNumber",
            "issuer" : "issuer",
            "group_id" : "groupId"
          } ],
          "name" : "name",
          "address" : "address",
          "city" : "city",
          "postal_code" : "postalCode",
          "state" : "state",
          "country" : "country",
          "coordinates" : {
            "latitude" : "12.34",
            "longitude" : "-56.7890"
          },
          "related_locations" : [ {
            "latitude" : "12.34",
            "longitude" : "-56.7890",
            "name" : {
              "language" : "language",
              "text" : "text"
            }
          } ],
          "parking_type" : $parkingType,
          "evses" : [ {
            "uid" : "uid",
            "evse_id" : "evseId",
            "status" : "AVAILABLE",
            "status_schedule" : [ {
              "period_begin" : "2025-01-02T13:45:59.708Z",
              "period_end" : "2025-01-02T13:45:59.708Z",
              "status" : "CHARGING"
            } ],
            "capabilities" : [ $capabilities ],
            "connectors" : [ ${connector("""["tariffId"]""", connectorType)} ],
            "floor_level" : "floorLevel",
            "coordinates" : {
              "latitude" : "12.34",
              "longitude" : "-56.7890"
            },
            "physical_reference" : "physicalReference",
            "directions" : [ {
              "language" : "language",
              "text" : "text"
            } ],
            "parking_restrictions" : [ $parkingRestrictions ],
            "images" : [ {
              "url" : "url",
              "thumbnail" : "thumbnail",
              "category" : $imageCategory,
              "type" : "image/png",
              "width" : 123,
              "height" : 456
            } ],
            "last_updated" : "2025-01-02T13:45:59.708Z"
          } ],
          "directions" : [ {
            "language" : "language",
            "text" : "text"
          } ],
          "operator" : {
            "name" : "name",
            "website" : "https://www.example.com",
            "logo" : {
              "url" : "url",
              "thumbnail" : "thumbnail",
              "category" : "LOCATION",
              "type" : "image/png",
              "width" : 123,
              "height" : 456
            }
          },
          "suboperator" : {
            "name" : "name",
            "website" : "https://www.example.com",
            "logo" : {
              "url" : "url",
              "thumbnail" : "thumbnail",
              "category" : "LOCATION",
              "type" : "image/png",
              "width" : 123,
              "height" : 456
            }
          },
          "owner" : {
            "name" : "name",
            "website" : "https://www.example.com",
            "logo" : {
              "url" : "url",
              "thumbnail" : "thumbnail",
              "category" : "LOCATION",
              "type" : "image/png",
              "width" : 123,
              "height" : 456
            }
          },
          "facilities" : [ $facilities ],
          "time_zone" : "Europe/Paris",
          "opening_times" : {
            "regular_hours" : [ {
              "weekday" : 1,
              "period_begin" : "00:00",
              "period_end" : "23:59"
            } ],
            "twentyfourseven" : false,
            "exceptional_openings" : [ {
              "period_begin" : "2025-01-02T13:45:59.708Z",
              "period_end" : "2025-02-01T11:00:00Z"
            } ],
            "exceptional_closings" : [ {
              "period_begin" : "2025-01-02T13:45:59.708Z",
              "period_end" : "2025-02-01T11:00:00Z"
            } ]
          },
          "charging_when_closed" : true,
          "images" : [ {
            "url" : "url",
            "thumbnail" : "thumbnail",
            "category" : "LOCATION",
            "type" : "image/png",
            "width" : 123,
            "height" : 456
          } ],
          "energy_mix" : {
            "is_green_energy" : true,
            "energy_sources" : [ {
              "source" : "NUCLEAR",
              "percentage" : 100
            } ],
            "environ_impact" : [ {
              "category" : "NUCLEAR_WASTE",
              "amount" : 100
            } ],
            "supplier_name" : "supplierName",
            "energy_product_name" : "energyProductName"
          },
          "last_updated" : "2025-01-01T10:00:00.123Z"
        }
    """.trimIndent()

    val token = """
        {
          "country_code" : "FR",
          "party_id" : "ABC",
          "uid" : "uid",
          "type" : "RFID",
          "contract_id" : "contractId",
          "visual_number" : "visualNumber",
          "issuer" : "issuer",
          "group_id" : "groupId",
          "valid" : true,
          "whitelist" : "ALLOWED",
          "language" : "language",
          "default_profile_type" : "GREEN",
          "energy_contract" : {
            "supplier_name" : "supplierName",
            "contract_id" : "contractId"
          },
          "last_updated" : "2025-01-02T13:45:59.708Z"
        }
    """.trimIndent()

    val authorizationInfo = """
        {
          "allowed" : "NOT_ALLOWED",
          "token" : $token,
          "location" : {
            "location_id" : "locationId",
            "evse_uids" : [ "evseUid" ]
          },
          "authorization_reference" : "authorizationReference",
          "info" : {
            "language" : "language",
            "text" : "text"
          }
        }
    """.trimIndent()

    val tariff = """
        {
          "id" : "id",
          "type" : "PROFILE_FAST",
          "party_id" : "ABC",
          "country_code" : "FR",
          "currency" : "currency",
          "tariff_alt_text" : [ {
            "language" : "language",
            "text" : "text"
          } ],
          "tariff_alt_url" : "https://example.com/tariffs",
          "min_price" : {
            "excl_vat" : 10.00,
            "incl_vat" : 12.34
          },
          "max_price" : {
            "excl_vat" : 10.00,
            "incl_vat" : 12.34
          },
          "elements" : [ {
            "price_components" : [ {
              "type" : "PARKING_TIME",
              "price" : 123,
              "vat" : 456,
              "step_size" : 23
            } ],
            "restrictions" : {
              "start_time" : "08:00",
              "end_time" : "20:00",
              "start_date" : "2025-01-01",
              "end_date" : "2025-12-31",
              "min_kwh" : 3.0,
              "max_kwh" : 22.0,
              "min_power" : 100.0,
              "max_power" : 200.0,
              "min_duration" : 300,
              "max_duration" : 7200,
              "day_of_week" : [ "SATURDAY" ],
              "reservation" : "RESERVATION"
            }
          } ],
          "start_date_time" : "2025-01-02T13:45:59.708Z",
          "end_date_time" : "2025-01-02T14:45:59.708Z",
          "energy_mix" : {
            "is_green_energy" : true,
            "energy_sources" : [ {
              "source" : "NUCLEAR",
              "percentage" : 100
            } ],
            "environ_impact" : [ {
              "category" : "NUCLEAR_WASTE",
              "amount" : 100
            } ],
            "supplier_name" : "supplierName",
            "energy_product_name" : "energyProductName"
          },
          "last_updated" : "2025-01-02T13:45:59.708Z"
        }
    """.trimIndent()

    val chargingPeriod = """
        {
        "start_date_time" : "2025-01-02T13:45:59.708Z",
        "dimensions" : [ {
          "type" : "ENERGY",
          "volume" : 15.123
        } ]
      }
    """.trimIndent()

    val session = """
        {
          "country_code" : "countryCode",
          "party_id" : "partyId",
          "id" : "id",
          "start_date_time" : "2025-01-02T13:45:59.708Z",
          "end_date_time" : "2025-01-02T13:45:59.708Z",
          "kwh" : 15.123,
          "cdr_token" : {
            "country_code" : "countryCode",
            "party_id" : "partyId",
            "uid" : "uid",
            "type" : "AD_HOC_USER",
            "contract_id" : "contractId"
          },
          "auth_method" : "COMMAND",
          "authorization_reference" : "12345",
          "location_id" : "LOC1",
          "evse_uid" : "EVSE1",
          "connector_id" : "CONN1",
          "meter_id" : "METER1",
          "currency" : "currency",
          "charging_periods" : [ $chargingPeriod ],
          "total_cost" : {
            "excl_vat" : 10.00,
            "incl_vat" : 12.34
          },
          "status" : "COMPLETED",
          "last_updated" : "2025-01-02T13:45:59.708Z"
        }
    """.trimIndent()

    val cdr = """
        {
          "id" : "id",
          "country_code" : "countryCode",
          "party_id" : "partyId",
          "start_date_time" : "2025-01-02T13:45:59.708Z",
          "end_date_time" : "2025-01-02T13:45:59.708Z",
          "session_id" : "sessionId",
          "cdr_token" : {
            "country_code" : "countryCode",
            "party_id" : "partyId",
            "uid" : "uid",
            "type" : "AD_HOC_USER",
            "contract_id" : "contractId"
          },
          "auth_method" : "COMMAND",
          "authorization_reference" : "authorizationReference",
          "cdr_location" : {
            "id" : "id",
            "name" : "name",
            "address" : "address",
            "city" : "city",
            "postal_code" : "postalCode",
            "state" : "state",
            "country" : "country",
            "coordinates" : {
              "latitude" : "12.34",
              "longitude" : "-56.7890"
            },
            "evse_uid" : "evseUid",
            "evse_id" : "evseId",
            "connector_id" : "connectorId",
            "connector_standard" : "IEC_62196_T2",
            "connector_format" : "SOCKET",
            "connector_power_type" : "AC_2_PHASE_SPLIT"
          },
          "meter_id" : "meterId",
          "currency" : "EUR",
          "tariffs" : [ {
            "id" : "id",
            "type" : "PROFILE_FAST",
            "party_id" : "ABC",
            "country_code" : "FR",
            "currency" : "currency",
            "tariff_alt_text" : [ {
              "language" : "language",
              "text" : "text"
            } ],
            "tariff_alt_url" : "https://example.com/tariffs",
            "min_price" : {
              "excl_vat" : 10.00,
              "incl_vat" : 12.34
            },
            "max_price" : {
              "excl_vat" : 10.00,
              "incl_vat" : 12.34
            },
            "elements" : [ {
              "price_components" : [ {
                "type" : "PARKING_TIME",
                "price" : 123,
                "vat" : 456,
                "step_size" : 23
              } ],
              "restrictions" : {
                "start_time" : "08:00",
                "end_time" : "20:00",
                "start_date" : "2025-01-01",
                "end_date" : "2025-12-31",
                "min_kwh" : 3.0,
                "max_kwh" : 22.0,
                "min_power" : 100.0,
                "max_power" : 200.0,
                "min_duration" : 300,
                "max_duration" : 7200,
                "day_of_week" : [ "SATURDAY" ],
                "reservation" : "RESERVATION"
              }
            } ],
            "start_date_time" : "2025-01-02T13:45:59.708Z",
            "end_date_time" : "2025-01-02T14:45:59.708Z",
            "energy_mix" : {
              "is_green_energy" : true,
              "energy_sources" : [ {
                "source" : "NUCLEAR",
                "percentage" : 100
              } ],
              "environ_impact" : [ {
                "category" : "NUCLEAR_WASTE",
                "amount" : 100
              } ],
              "supplier_name" : "supplierName",
              "energy_product_name" : "energyProductName"
            },
            "last_updated" : "2025-01-02T13:45:59.708Z"
          } ],
          "charging_periods" : [ $chargingPeriod ],
          "signed_data" : {
            "encoding_method" : "encodingMethod",
            "encoding_method_version" : 23,
            "public_key" : "publicKey",
            "signed_values" : [ {
              "nature" : "nature",
              "plain_data" : "plainData",
              "signed_data" : "signedData"
            } ],
            "url" : "url"
          },
          "total_cost" : {
            "excl_vat" : 10.00,
            "incl_vat" : 12.00
          },
          "total_fixed_cost" : {
            "excl_vat" : 10.00,
            "incl_vat" : 12.00
          },
          "total_energy" : 20.0,
          "total_energy_cost" : {
            "excl_vat" : 10.00,
            "incl_vat" : 12.00
          },
          "total_time" : 3600,
          "total_time_cost" : {
            "excl_vat" : 10.00,
            "incl_vat" : 12.00
          },
          "total_parking_time" : 1800,
          "total_parking_cost" : {
            "excl_vat" : 10.00,
            "incl_vat" : 12.00
          },
          "total_reservation_cost" : {
            "excl_vat" : 10.00,
            "incl_vat" : 12.00
          },
          "remark" : "remark",
          "invoice_reference_id" : "invoiceReferenceId",
          "credit" : true,
          "credit_reference_id" : "creditReferenceId",
          "home_charging_compensation" : true,
          "last_updated" : "2025-01-02T13:45:59.708Z"
        }
    """.trimIndent()

    val activeChargingProfileResult = """
        {
          "result" : "REJECTED",
          "profile" : {
            "start_date_time" : "2025-01-02T13:45:59.708Z",
            "charging_profile" : {
              "start_date_time" : "2025-01-02T13:45:59.708Z",
              "duration" : 12,
              "charging_rate_unit" : "A",
              "min_charging_rate" : 123,
              "charging_profile_period" : [ {
                "start_period" : 32,
                "limit" : 123
              } ]
            }
          }
        }
    """.trimIndent()

    val chargingProfileResponse = """
        {
          "result" : "UNKNOWN_SESSION",
          "timeout" : 12
        }
    """.trimIndent()
    val chargingProfileResult = """
        {
          "result" : "ACCEPTED"
        }
    """.trimIndent()

    val clearProfileResult = """
        {
          "result" : "UNKNOWN"
        }
    """.trimIndent()

    val setChargingProfile = """
        {
          "charging_profile" : {
            "start_date_time" : "2025-01-02T13:45:59.708Z",
            "duration" : 12,
            "charging_rate_unit" : "A",
            "min_charging_rate" : 123,
            "charging_profile_period" : [ {
              "start_period" : 32,
              "limit" : 123
            } ]
          },
          "response_url" : "responseUrl"
        }
    """.trimIndent()

    val cancelReservation = """
        {
          "response_url" : "responseUrl",
          "reservation_id" : "reservationId"
        }
    """.trimIndent()

    val commandResponse = """
        {
          "result" : "ACCEPTED",
          "timeout" : 314,
          "message" : [ {
            "language" : "language",
            "text" : "text"
          } ]
        }
    """.trimIndent()

    val commandResult = """
        {
          "result" : "REJECTED",
          "message" : [ {
            "language" : "language",
            "text" : "text"
          } ]
        }
    """.trimIndent()

    val reserveNow = """
        {
          "response_url" : "responseUrl",
          "token" : $token,
          "expiry_date" : "2025-01-02T13:45:59.708Z",
          "reservation_id" : "reservationId",
          "location_id" : "locationId",
          "evse_uid" : "evseUid",
          "authorization_reference" : "authorizationReference"
        }
    """.trimIndent()

    val startSession = """
        {
          "response_url" : "responseUrl",
          "token" : $token,
          "location_id" : "locationId",
          "evse_uid" : "evseUid",
          "connector_id" : "connectorId",
          "authorization_reference" : "authorizationReference"
        }
    """.trimIndent()

    val stopSession = """
        {
          "response_url" : "responseUrl",
          "session_id" : "sessionId"
        }
    """.trimIndent()

    val unlockConnector = """
        {
          "response_url" : "responseUrl",
          "location_id" : "locationId",
          "evse_uid" : "evseUid",
          "connector_id" : "connectorId"
        }
    """.trimIndent()

    val credentials = """
        {
          "token" : "token",
          "url" : "url",
          "roles" : [ {
            "role" : "CPO",
            "business_details" : {
              "name" : "name",
              "website" : "https://www.example.com",
              "logo" : {
                "url" : "url",
                "thumbnail" : "thumbnail",
                "category" : "LOCATION",
                "type" : "image/png",
                "width" : 123,
                "height" : 456
              }
            },
            "party_id" : "partyId",
            "country_code" : "countryCode"
          } ]
        }
    """.trimIndent()

    val versionDetails = """
        {
          "version" : "version",
          "endpoints" : [ {
            "identifier" : "credentials",
            "role" : "RECEIVER",
            "url" : "url"
          } ]
        }
    """.trimIndent()

    fun ocpiResponseBody(data: String) = """
        {
          "data" : $data,
          "status_code" : 1000,
          "status_message" : "message",
          "timestamp" : "2025-01-02T13:45:59.708Z"
        }
    """.trimIndent()
}

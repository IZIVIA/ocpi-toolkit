package com.izivia.ocpi.toolkit.modules.cdr.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit.common.CiString


/**
 * @property uid (max-length=36) Unique ID by which this Token can be identified.
This is the field used by the CPO’s system (RFID reader on the Charge Point) to
identify this token.
Currently, in most cases: type=RFID, this is the RFID hidden ID as read by the
RFID reader, but that is not a requirement.
If this is a type=APP_USER Token, it will be a unique, by the eMSP, generated
ID.
 *@property type Type of the token
 * @property contract_id (max-length=36) Uniquely identifies the EV driver contract token within the eMSP’s platform (and
suboperator platforms). Recommended to follow the specification for eMA ID
from "eMI3 standard version V1.0" (http://emi3group.com/documents-links/) "Part 2: business objects."
 */
@Partial
data class CdrToken(
    val uid: CiString,
    val type: TokenType,
    val contract_id: CiString,
)

import {AbstractDTO} from "@/api/abstractDTO";

export class UnavailabilityDTO extends AbstractDTO {
    /**
     * @param {number} id
     * @param {number} version
     * @param {string} description
     * @param {boolean} valid
     * @param {date} dateTimeFrom
     * @param {date} dateTimeTo
     * @param {number} entertainerId
     */
    constructor(id, version, dateTimeFrom, dateTimeTo, description, entertainerId, valid) {
        super(id, version);
        this.description = description;
        this.dateTimeFrom = dateTimeFrom;
        this.dateTimeTo = dateTimeTo;
        this.valid = valid;
        this.entertainerId = entertainerId;
    }

    static fromResponse(unavailabilityDTO) {
        return new UnavailabilityDTO(
            unavailabilityDTO.id,
            unavailabilityDTO.version,
            unavailabilityDTO.dateTimeFrom,
            unavailabilityDTO.dateTimeTo,
            unavailabilityDTO.description,
            unavailabilityDTO.entertainerId,
            unavailabilityDTO.valid);
    }
}
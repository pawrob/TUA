import {AbstractDTO} from "@/api/abstractDTO";

export class OfferAvailabilityDTO extends AbstractDTO {

    constructor(id, version, weekDay, hoursFrom, hoursTo) {
        super(id, version);
        this.weekDay = weekDay;
        this.hoursFrom = hoursFrom;
        this.hoursTo = hoursTo;
    }
}
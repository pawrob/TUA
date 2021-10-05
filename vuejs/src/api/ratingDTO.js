import {AbstractDTO} from "@/api/abstractDTO";

export class RatingDTO extends AbstractDTO {
    /**
     * @param {number} id
     * @param {number} version
     * @param {int} rating
     * @param {string} comment
     */
    constructor(id, version, rating, comment) {
        super(id, version);
        this.rating = rating;
        this.comment = comment;
    }

    static fromResponse(ratingData) {
        return new RatingDTO(
            ratingData.id,
            ratingData.version,
            ratingData.rating,
            ratingData.comment);
    }
}
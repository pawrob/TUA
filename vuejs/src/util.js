/**
 *
 * @param {Date} from
 * @param {Date} to
 * @param {Date} value
 */
export function dateInRange(from, to, value) {
    return (from <= value) && (value <= to);
}

/**
 *
 * @param {Date} a
 * @param {Date} b
 */
export function minDate(a, b) {
    if (a < b) {
        return a;
    } else {
        return b;
    }
}

/**
 *
 * @param {Date} a
 * @param {Date} b
 */
export function maxDate(a, b) {
    if (a > b) {
        return a;
    } else {
        return b;
    }
}

/**
 *
 * @param {Date} from
 * @param {Date} to
 * @param {Date} value
 */
export function dateInRangeOnlyDate(from, to, value) {
    console.log({from, to, value})
    return (from.getDate() <= value.getDate()) && (value.getDate() <= to.getDate());
}
// 12-100 chars with at least one lowercase, uppercase, digit and special character; no whitespace
export const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9\s])\S{12,100}$/;

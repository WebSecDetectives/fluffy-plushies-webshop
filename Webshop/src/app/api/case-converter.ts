/**
 * Recursively converts JSON keys between camelCase and snake_case.
 * Only plain objects and arrays are converted; all other values
 * (including strings, File, Blob, FormData) pass through untouched.
 */

export function toSnakeCaseKeys(value: unknown): unknown {
  return convertKeys(value, camelToSnakeKey);
}

export function toCamelCaseKeys(value: unknown): unknown {
  return convertKeys(value, snakeToCamelKey);
}

function camelToSnakeKey(key: string): string {
  return key.replace(/[A-Z]/g, upper => `_${upper.toLowerCase()}`);
}

function snakeToCamelKey(key: string): string {
  return key.replace(/_([a-z0-9])/g, (_, char) => char.toUpperCase());
}

function convertKeys(value: unknown, convertKey: (key: string) => string): unknown {
  if (Array.isArray(value)) {
    return value.map(item => convertKeys(item, convertKey));
  }
  if (isPlainObject(value)) {
    return Object.fromEntries(
      Object.entries(value).map(([key, val]) => [convertKey(key), convertKeys(val, convertKey)])
    );
  }
  return value;
}

function isPlainObject(value: unknown): value is Record<string, unknown> {
  return (
    value !== null &&
    typeof value === 'object' &&
    (Object.getPrototypeOf(value) === Object.prototype || Object.getPrototypeOf(value) === null)
  );
}
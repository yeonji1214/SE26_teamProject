const DEFAULT_API_BASE_URL = "http://localhost:8080";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL?.replace(/\/$/, "") ??
  DEFAULT_API_BASE_URL;

export class ApiError extends Error {
  readonly status: number;
  readonly path?: string;

  constructor(message: string, status: number, path?: string) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.path = path;
  }
}

type QueryValue = string | number | boolean | null | undefined;

export type QueryParams = Record<string, QueryValue>;

interface ApiErrorBody {
  error?: string;
  message?: string;
  status?: number;
  path?: string;
  timestamp?: string;
}

interface RequestOptions {
  method?: "GET" | "POST" | "PATCH" | "DELETE";
  body?: unknown;
  query?: QueryParams;
}

export async function apiRequest<T>(
  path: string,
  options: RequestOptions = {}
): Promise<T> {
  const url = buildUrl(path, options.query);

  const response = await fetch(url, {
    method: options.method ?? "GET",
    headers: buildHeaders(options.body),
    body: options.body === undefined ? undefined : JSON.stringify(options.body),
  });

  if (!response.ok) {
    throw await buildApiError(response);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}

export function buildQueryParams(params: QueryParams): string {
  const searchParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null || value === "") {
      return;
    }

    searchParams.set(key, String(value));
  });

  return searchParams.toString();
}

function buildUrl(path: string, query?: QueryParams): string {
  const normalizedPath = path.startsWith("/") ? path : `/${path}`;
  const queryString = query ? buildQueryParams(query) : "";

  return `${API_BASE_URL}${normalizedPath}${queryString ? `?${queryString}` : ""}`;
}

function buildHeaders(body: unknown): HeadersInit {
  if (body === undefined) {
    return {};
  }

  return {
    "Content-Type": "application/json",
  };
}

async function buildApiError(response: Response): Promise<ApiError> {
  const fallbackMessage = `HTTP request failed with status ${response.status}`;

  try {
    const body = (await response.json()) as ApiErrorBody;

    return new ApiError(
      body.message ?? body.error ?? fallbackMessage,
      body.status ?? response.status,
      body.path
    );
  } catch {
    return new ApiError(fallbackMessage, response.status);
  }
}
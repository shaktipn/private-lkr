package com.suryadigital.leo.kedwig

/**
 * Defined the HTTP method for a given API call.
 */
enum class Method {
    /**
     * Denotes that the API call is being made to retrieve data with a response body.
     */
    GET,

    /**
     * Denotes that the API call is being made to update a resource entirely on the remote API server.
     */
    PUT,

    /**
     * Denotes that the API call is being made to create resource on the remote API server.
     */
    POST,

    /**
     * Denotes that the API call is being made to delete a resource on the remote API server.
     */
    DELETE,

    /**
     * Denotes that the API call is being made to determine the possible communication options for the remote API server.
     */
    OPTIONS,

    /**
     * Denotes that the API call is being made to retreive the data, but no response body is present. This is generally used as a pre-call for [GET] to determine the size of the response.
     */
    HEAD,

    /**
     * Denotes that the API call is being made to establish a two-way connection with the remote API server.
     */
    CONNECT,

    /**
     * Denotes that the API call is being made for troubleshooting purposes.
     */
    TRACE,
}

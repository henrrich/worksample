Feature: User can fetch artist profile and cover arts of all albums

  @positive
  Scenario Outline: client makes call to GET /artistinfo/ with valid artist id
    When the client calls api GET "/api/v1/artistinfo/" with "<artist id>"
    Then the client receives status code of 200
    And the response body is identical to the expected json "<file>"

    Examples:
      | artist id                            | file               |
      | 65f4f0c5-ef9e-490c-aee3-909e7ae6b2ab | bdd/metallica.json |
      | 5b11f4ce-a62d-471e-81fc-a69a8278c7da | bdd/nirvana.json |
      | 410c9baf-5469-44f6-9852-826524b80c61 | bdd/autechre.json |

  @negative
  Scenario Outline: client makes call to GET /artistinfo/ with artist id that does not exist
    When the client calls api GET "/api/v1/artistinfo/" with "<artist id>" that does not exist
    Then the client receives status code of 404
    And the response body contains the expected json "<file>"

    Examples:
      | artist id                            | file                      |
      | 65f4f0c5-ef9e-490c-aee3-909e7ae6b2ac | bdd/artist_not_found.json |

  @negative
  Scenario Outline: client makes call to GET /artistinfo/ with artist id in wrong format
    When the client calls api GET "/api/v1/artistinfo/" with invalid "<artist id>"
    Then the client receives status code of 400
    And the response body contains the expected json "<file>"

    Examples:
      | artist id               | file                      |
      | 65f4f0c5-ef9e-490c-aee3 | bdd/invalid_artist_mbid.json |
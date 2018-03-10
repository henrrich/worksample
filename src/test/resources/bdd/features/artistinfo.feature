Feature: User can fetch artist profile and cover arts of all albums

  @positive
  Scenario Outline: client makes call to GET /artistinfo/
    When the client calls api GET "/api/v1/artistinfo/" with "<artist id>"
    Then the client receives status code of 200
    And the response body is identical to the expected json "<file>"

    Examples:
      | artist id                            | file               |
      | 65f4f0c5-ef9e-490c-aee3-909e7ae6b2ab | bdd/metallica.json |

  @negative
  Scenario Outline: client makes call to GET /artistinfo/
    When the client calls api GET "/api/v1/artistinfo/" with "<artist id>" that does not exist
    Then the client receives status code of 404
    And the error response body is identical to the expected json "<file>"

    Examples:
      | artist id                            | file                      |
      | 65f4f0c5-ef9e-490c-aee3-909e7ae6b2ac | bdd/artist_not_found.json |
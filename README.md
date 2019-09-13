# KotlinServer API Documentation
**Esse documento tem como objetivo descrever todas as funcionalidadaes presentes na API construída neste repositório.**

- [Conjunto de ambientes](#conjunto-de-ambientes)
- [URL base de comunicação](#url-base-de-comunicação)
- [Descrição de utilização da API](#descrição-de-utilização-da-api)
  * [Funções de atividades:](#funções-de-atividades)
    + [_getActivityFromUser_](#getactivityfromuser)
  * [Funções de challenge:](#funções-de-challenge)
    + [_submitChallenge_](#submitchallenge)
    + [_getChallenge_](#getchallenge)
    + [_updateAnswers_](#updateanswers)
  * [Funções de explore:](#funções-de-explore)
    + [_getRandomPosts_](#getrandomposts)
  * [Funções de feed:](#funções-de-feed)
    + [_getPostsForFeed_](#getpostsforfeed)
  * [Funções de torneio:](#funções-de-torneio)
    + [_submitGame_](#submitgame)
  * [Funções de login:](#funções-de-login)
    + [_authenticate_](#authenticate)
  * [Funções de post:](#funções-de-post)
    + [_submitPost_](#submitpost)
    + [_updatePostComments_](#updatepostcomments)
    + [_deletePost_](#deletepost)
  * [Funções de questões:](#funções-de-questões)
    + [_questions_](#questions)
    + [_videos_](#videos)
    + [_getVideoById_](#getvideobyid)
    + [_getRanking_](#getranking)
  * [Funções de report:](#funções-de-report)
    + [_submitReport_](#submitReport)
  * [Funções de trivia:](#fun--es-de-trivia)
    + [_submitTrivia_](#submitTrivia)
    + [_triviaQuestions_](#triviaquestions)
    + [_updateComments_](#updatecomments)
    + [_updateRating_](#updaterating)
  * [Funções de user:](#funções-de-user)
    + [_getUserById_](#getuserbyid)
    + [_getUsersByNameUsingLocation_](#getusersbynameusinglocation)
    + [_getChallengesFromUserById_](#getchallengesfromuserbyid)
	+ [_getPostsFromUser_](#getpostsfromuser)
    + [_updateLocation_](#updatelocation)
    + [_updateSchool_](#updateschool)
    + [_updateAge_](#updateage)
    + [_updateYoutubeChannel_](#updateyoutubechannel)
    + [_updateInstagramProfile_](#updateinstagramprofile)
    + [_updateDescription_](#updatedescription)
    + [_updateWebsite_](#updatewebsite)
    + [_updateProducerToBeFollowed_](#updateproducertobefollowed)
    + [_updateProducerToBeUnfollowed_](#updateproducertobeunfollowed)
- [HTTP STATUS Codes Used](#http-status-codes-used)


# Conjunto de ambientes
Esta API é utilizada por **2** ambientes ao total, sendo eles:
- Liceu-Production
- Liceu-Staging

---------------------

# URL base de comunicação
Para comunicação com os ambientes é utilizado a seguintes URL base: 
- https://liceu-staging.herokuapp.com

-------------------

# Descrição de utilização da API

**OBSERVAÇÃO: Toda função criada nesse repositório necessita de autenticação. Logo todo header de requisição necessita de:**  
- "API_KEY": apiKey,  
- "Authorization": accessToken  


##  Funções de atividades:

- ###  _getActivityFromUser_


Retornar todas as atividades realizadas pelo ou para o usuário  
**URL:** /v2/activity/{userId}  
**METHOD:** GET  
 **URL Parameters:**

| Parameter  | Type  |
| ------------ | ------------ |
| amount  | Int  |

**Data Retrieved Example:**

<pre>
{
	"id": "0a1449a4123dsvad5ae1e462"
	"userId": "0a1449a4123dsvad5ae1e462"
	"type": "followedUser"
	"params": {
		"followedBy": "39c898725b75357a571d4cc2"
	}
}
</pre>

------------------------

## Funções de challenge:

- ### _submitChallenge_
Desafio direto de um usuário para outro usuário
**URL:**/v2/challenge  
**METHOD:** POST  

| Parameter  | Type  |
| ------------ | ------------ |
| challengerId  | String  |

**BODY:**  

<pre>
	{
		"challengedId" : "0a1449a4123dsvad5ae1e462"
	}
</pre>

**Data Retrieved Example:**

<pre>
{
    "id": "09c54aa21414357a571d4cc1",
    "challenger": "37235321avc76abebce3f6e6",
    "challenged": "3a1449a4bdb40ahjkue1e431",
    "answersChallenger": [
        "2"
    ],
    "answersChallenged": [],
    "scoreChallenger": 1,
    "scoreChallenged": null,
    "triviaQuestionsUsed": [
        {
            "id": "0a1449a4bdb40abd5ae1e411",
            "userId": "37235b2a67c76abebce3f6e6",
            "question": "1+1?",
            "correctAnswer": "2",
            "wrongAnswer": "1",
            "tags": [
                "matematica",
                "algebra"
            ],
            "comments": [
                {
                    "id": "0a4449a4bdb40abd5ae1e461",
                    "userId": "3a1449a4bdb40abd5ae1e431",
                    "author": "user1",
                    "comment": "essa questao e boa"
                }
            ],
            "likes": null,
            "dislikes": null
        }
    ]
}
</pre>

 - ###  _getChallenge_

Retornar challenge para início de challenge entre o usuário autenticado e outro usuário  
**URL:**/v2/challenge  
**METHOD:** GET  

**Data Retrieved Example:**

<pre>
{
    "id": "09c54aa21414357a571d4cc1",
    "challenger": "37235321avc76abebce3f6e6",
    "challenged": "3a1449a4bdb40ahjkue1e431",
    "answersChallenger": [
        "2"
    ],
    "answersChallenged":  [
        "1"
    ],
    "scoreChallenger": 1,
    "scoreChallenged": 0,
    "triviaQuestionsUsed": [
        {
            "id": "0a1449a4bdb40abd5ae1e411",
            "userId": "37235b2a67c76abebce3f6e6",
            "question": "1+1?",
            "correctAnswer": "2",
            "wrongAnswer": "1",
            "tags": [
                "matematica",
                "algebra"
            ],
            "comments": [
                {
                    "id": "0a4449a4bdb40abd5ae1e461",
                    "userId": "3a1449a4bdb40abd5ae1e431",
                    "author": "user1",
                    "comment": "essa questao e boa"
                }
            ],
            "likes": null,
            "dislikes": null
        }
    ]
}
</pre>

 - ### _updateAnswers_

Atualizar conjunto de resspostas do usuário autenticado  
**URL:** v2/challenge/{challengeId}  
 **URL Parameters:**

| Parameter  | Type  |
| ------------ | ------------ |
| challengeId  | String  |

**METHOD:** PUT  
**BODY:**  
<pre>
	{
		"answers" : [
				{
					 "Nazismo",
					 "Revolução francesa",
					 "Cateto adjacente",
					 "Simbiose"
				 }
		]
	}
</pre>


------------------------

## Funções de explore:


  - ### _getRandomPosts_

Retornar posts aleatórios para o usuário  
**URL:** /v2/explore  
**METHOD:** GET  
 **URL Parameters:**

| Parameter  | Type  |
| ------------ | ------------ |
| amount  | Int  |

**Data Retrieved Example:**
<pre>
{
        "id": "09c54daaa75357a581d4ca2",
        "userId": "37235321avc76abebce3f6e6",
        "type": "text",
        "description": "teste de texto",
        "imageURL": null,
        "video": {
            "videoUrl": null,
            "thumbnails": {
                "high": null,
                "default": null,
                "medium": null
            }
        },
        "submissionDate": "2019-08-27T11:40:20.000+0000",
        "comments": null,
        "questions": [
            {
                "question": "Qual o primeiro nome de Einstein?",
                "correctAnswer": "Albert",
                "otherAnswers": [
                    "José",
                    "Albertonio",
                    "Lucas"
                ]
            },
            {
                "question": "Qual o primeiro nome de Newton?",
                "correctAnswer": "Isaac",
                "otherAnswers": [
                    "José",
                    "Albertonio",
                    "Albert"
                ]
            }
        ]
    }
</pre>


------------------------

## Funções de feed:

  - ### _getPostsForFeed_

Retornar posts aleatórios para o usuário  
**URL:** /v2/feed  
**METHOD:** GET  
 **URL Parameters:**

| Parameter  | Type  |
| ------------ | ------------ |
| before  | String  |
| amount  | Int  |

**Data Retrieved Example:**
<pre>
{
        "id": "09c54d325b75357a581d4ca4",
        "userId": "37235b2a67c76abebce3f6e3",
        "type": "video",
        "description": "teste de video",
        "imageURL": null,
        "video": {
            "videoUrl": "www.youtube.com/testeVideo",
            "thumbnails": {
                "high": "high",
                "default": "default",
                "medium": "medium"
            }
        },
        "submissionDate": "2019-08-27T12:40:20.000+0000",
        "comments": null,
        "questions": null
}
</pre>

------------------------

## Funções de torneio:

  - ### _submitGame_

Inserir um jogo de torneio realizado pelo usuário  
**URL:** /v2/game  
**METHOD:** POST  
**BODY:** 
<pre>
	{
		 "answers" :  [
				 {
					"questionId": "821660feff444347",
					"selectedAnswer": "1",
					"correctAnswer": "2"
				 },
				 {
					"questionId": "821660feff555347",
					"selectedAnswer": "2",
					"correctAnswer": "2"
				}
		],
	   	"timeSpent": 10
	}
</pre>


------------------------

## Funções de login:

 - ###  _authenticate_

Realizar login do usuário por autenticação externa  
**URL:** /v2/login  
**METHOD:** POST  
**BODY:** 
<pre>
     {
	"accessToken": "38a41b13f063128ee2568991aeb4253c97d9c181ac669e20c5e6f396a544df81f2b9e641e13829094e3a971dffe6bef8abbb7c0ff266c4240f9712cc17a69f72b0287f4849178426aafb66bebbbe042a36e3aa33
be716ade4d6f88477e0ed869bff2626b0f2a2f185c850a75af7dc6fdb1ef56c61c1be15b08ee50f937a706",
	"method":  "google"
     }
</pre>

-----------------------

## Funções de post:

  - ### _submitPost_

Inserir um post do usuário  
**URL:** /v2/post   
**METHOD:** POST  
**BODY:** 
<pre>
	{
		"type": "video",
		"videoUrl": "www.youtube.com/meuvideo1",
		"description": "video legal",
		"hasQuestions": "True"
		 "questions" :  [
					{
						"questionId": "821660feff444347",
						"correctAnswer": "Brasil",
						"otherAnswer":  [
							"Espanha",
							"Holanda",
							"USA"
						]
					},
					{
						"questionId": "821660feff555347",
						"correctAnswer": "Tênis",
						"otherAnswer":  [
							"Futebol",
							"Vôlei",
							"Basquete"
						]
					}
		]	
	}
</pre>


  - ### _updatePostComments_

Atualizar comentários de um determinado post  
 **URL:**/v2/post/{postId}/comment  
 **URL Parameters:**  

| Parameter  | Type  |
| ------------ | ------------ |
| postId  | String  |

**METHOD:** PUT  
**BODY:** 
<pre>
     {
	"comment": "vídeo muito bom"
      }
</pre>

 
 - ###  _deletePost_

Remover um determinado post  
 **URL:**/v2/post/{postId}  
 **URL Parameters:**  

| Parameter  | Type  |
| ------------ | ------------ |
| postId  | String  |

**METHOD:** DELETE  


-----------------------

## Funções de questões:

 - ###  _questions_

Retornar questões baseados em um conjunto de tags  
 **URL:** /v2/question    
  **URL Parameters:**  

| Parameter  | Type  |
| ------------ | ------------ |
| tags | String |
| amount  | Int|

**METHOD:** GET  
**Data Retrieved Example:**
<pre>
[
    {
        "id": "07235b2a12345abebce3f6e6",
        "view": "https://url3.com",
        "source": "ENEM",
        "variant": "AZUL",
        "edition": 2015,
        "number": 15,
        "domain": "linguagens",
        "answer": 3,
        "tags": [],
        "stage": 1,
        "width": 100,
        "height": 200
    }
]
</pre>

 - ###  _videos_

Retornar vídeos referentes a uma questão  
 **URL:** /v2/{questionId}/videos  
  **URL Parameters:**  

| Parameter  | Type  |
| ------------ | ------------ |
| questionId | String |
| start | Int |
| amount | Int |

**METHOD:** GET  
**Data Retrieved Example:**
<pre>
[
	{
	"terceiro",
                "title": "terceiro video",
                "description": "videoId2",
                "id": "07235b2a12345abebc1116e6"
                "aspectRation": 1.3f,
				 "thumbnails": {
					"high": "high",
					"default": "default",
					"medium": "medium"
				},
                "channel": {
                        "fisicaQuantica",
                        "07235b2a12345abebc1116e6"
                },
               "retrievalPosition": 1
	}
]
</pre>

  - ### _getVideoById_

Retornar video referente a um ID  
 **URL:** /v2/{questionId}  
 **URL Parameters:**  

| Parameter  | Type  |
| ------------ | ------------ |
| questionId | String |

**METHOD:** GET  
**Data Retrieved Example:**
<pre>
[
		{
		"terceiro",
					"title": "video questao 2",
					"description": "videoId2",
					"id": "0723522222345abebc1116e6"
					"aspectRation": 1.8f,
					 "thumbnails": {
						"high": "high",
						"default": "default",
						"medium": "medium"
					},
					"channel": {
							"fisicaMolecular",
							"07235b2a12345abebc1116e6"
					},
				   "retrievalPosition": 1
		}
]
</pre>


-----------------------


## Funções de ranking:


  - ### _getRanking_


Retornar lista de ranking  
 **URL:** /v2/ranking  
 **URL Parameters:**  

| Parameter  | Type  |
| ------------ | ------------ |
| month | Int |
| year | Int |
| amount | Int |

**METHOD:** GET  

**Data Retrieved Example:**
<pre>
 [
    {
        "id": "47235b2a67c76abebce3f6e3",
        "userId": "37235b2a67c76abebce3f6e3",
        "answers": [
            {
                "questionId": "0a1449a4bdb40abd5ae1e431",
                "correctAnswer": 2,
                "selectedAnswer": 2
            },
            {
                "questionId": "07235b2a67c76abebce3f6e6",
                "correctAnswer": 3,
                "selectedAnswer": 3
            }
        ],
        "submissionDate": "Mon Oct 14 08:20:20 BRT 2019",
        "timeSpent": 1
    },
    {
        "id": "47235b2a67c76abebce3f6e6",
        "userId": "37235b2a67c76abebce3f6e6",
        "answers": [
            {
                "questionId": "0a1449a4bdb40abd5ae1e431",
                "correctAnswer": 2,
                "selectedAnswer": 2
            },
            {
                "questionId": "07235b2a67c76abebce3f6e6",
                "correctAnswer": 3,
                "selectedAnswer": 3
            }
        ],
        "submissionDate": "Sat Oct 12 08:20:20 BRT 2019",
        "timeSpent": 3
    }
]
</pre>


-----------------------

##  Funções de report:

 - ###  _submitReport_

Inserir report de erro  
 **URL:** /v2/report  
**METHOD:** POST  
**BODY:** 
<pre>
     {
	"message": "erro no gabarito da questao",
	"tags": [
		{
			"question", "error", "answer"
		}
	],
	"params": [
		"questionId" : "41b13f063128ee2568"
		"button": "not working"
	]
     }
</pre>
 

-----------------------

## Funções de trivia:

 - ### _submitTrivia_
Inserir questão de trivia  
 **URL:** /v2/trivia  
**METHOD:** POST  
**BODY:**
<pre>
     {
	"question": "Qual o nome de Albert Einstein?"
	"correctAnswer": "Albert",
	"wrongAnswer": "Jorge",
	"tags": [
		"ciência",
		"vida"
	]
     }
</pre>


 - ### _triviaQuestions_
Retornar questões de trivia  
**URL:** /v2/trivia  
 **URL Parameters:**

| Parameter  | Type  |
| ------------ | ------------ |
| tags | String |
| amount | Int |

**METHOD:** GET  
**Data Retrieved Example:**
<pre>
[
    {
        "id": "0a1449a4bdb40abd5ae1e432",
        "userId": "37235b2a67c76abebce3f6e3",
        "question": "1+1+3 = ?",
        "correctAnswer": "5",
        "wrongAnswer": "2",
        "tags": [
            "matematica",
            "angulos",
            "trigonometria"
        ],
        "comments": null,
        "likes": null,
        "dislikes": null
    }
</pre>

 - ### _updateComments_
Atualizar comentários de uma determinada questão  
 **URL:** /v2/trivia   
 **URL Parameters:**  

| Parameter  | Type  |
| ------------ | ------------ |
| questionId  | String   |

**METHOD:** POST  
**BODY:** 
<pre>
     {
	"comment": "Essa questão é muito boa"
     }
</pre>


 - ### _updateRating_
Atualizar pontuação de uma determinada questão  
 **URL:** /v2/trivia/{questionId}/rating  
 **URL Parameters:**
 
| Parameter  | Type  |
| ------------ | ------------ |
| questionId  | String   |

**METHOD:** PUT  
**BODY:** 
<pre>
     {
	"rating": "6"
     }
</pre>


 -----------------------

## Funções de user:

 - ### _getUserById_
Retornar usuário referente ao ID  
**URL:** /v2/user/{userId}  
**URL Parameters:**  

| Parameter  | Type  |
| ------------ | ------------ |
| userId  | String   |

**METHOD:** GET

**Data Retrieved Example:**
<pre>
{
    "id": "37235b2a67c76abebce3f6e6",
    "name": "manitos1",
    "email": "user3@g.com",
    "picture": {
        "url": "https://picture3.jpg",
        "width": 200,
        "height": 200
    },
    "state": "BA",
    "school": "MARISTA",
    "age": 18,
    "youtubeChannel": "Jorginho",
    "instagramProfile": "jorge",
    "description": "alguma descrição maneira",
    "website": "www.umsite.com.br",
    "amountOfFollowers": 1,
    "amountOfFollowing": 2,
    "following": true
}
</pre>

 - ### _getUsersByNameUsingLocation_
Retornar usuários baseados em localização e nome   
 **URL:** /v2/user  
 **URL Parameters:**  

|  Parameter  |  Type |
| ------------ | ------------ |
| name  | String  |
|  longitude  |  String  |
| latitude   |  String  |
|   amount  |  Int  |

**METHOD:** GET  

**Data Retrieved Example:**
<pre>
{
    "id": "3723522227c76abebce3f6e6",
    "name": "manolo1",
    "email": "user2@gg.com",
    "picture": {
        "url": "https://picture3.jpg",
        "width": 200,
        "height": 200
    },
    "state": "BA",
    "school": "MARISTA",
    "age": 18,
    "youtubeChannel": "manolo1",
    "instagramProfile": "manolo12",
    "description": "alguma descrição maneira",
    "website": "www.umsite.com.br",
    "amountOfFollowers": 1,
    "amountOfFollowing": 2,
    "following": true
}
</pre>

 - ### _getChallengesFromUserById_
Retornar challenges de um usúario  
 **URL:** /v2/user/{userId}/challenge  
 **URL Parameters:**

| Parameter  | Type  |
| ------------ | ------------ |
| userId  | String   |

**METHOD:** GET

**Data Retrieved Example:**
<pre>
[
	{
        "id": "09b44d425b75357a571d4ce2",
        "challenger": "3a1449a4bdb40abd5ae1e431",
        "challenged": "37235b2a67c76abebce3f6e6",
        "answersChallenger": [
            "1"
        ],
        "answersChallenged": [],
        "scoreChallenger": 0,
        "scoreChallenged": null,
        "triviaQuestionsUsed": [
            {
                "id": "0a1449a4bdb40abd5ae1e461",
                "userId": "37235b2a67c76abebce3f6e3",
                "question": "1+2?",
                "correctAnswer": "3",
                "wrongAnswer": "0",
                "tags": [
                    "graficos",
                    "algebra"
                ],
                "comments": null,
                "likes": null,
                "dislikes": null
            }
        ]
    }
]
</pre>

 - ### _getPostsFromUser_
Retornar posts de um usuário  
**URL:** /v2/user/{userId}/posts  
**URL Parameters:**

| Parameter  | Type  |
| ------------ | ------------ |
| userId  | String   |

**METHOD:** GET  
**Data Retrieved Example:**
<pre>
 [
     {
        "id": "09c54d325b75357a581d4ca6",
        "userId": "37235b2a67c76abebce3f6e6",
        "type": "text",
        "description": "teste de texto 2",
        "imageURL": null,
        "video": {
            "videoUrl": null,
            "thumbnails": {
                "high": null,
                "default": null,
                "medium": null
            }
        },
        "submissionDate": "2019-08-27T13:40:20.000+0000",
        "comments": null,
        "questions": null
    }
]
</pre>

 - ### _updateLocation_
Atualizar localização de um determinado usuário  
 **URL:** /{userId}/locale  
 **URL Parameters:**  

| Parameter  | Type  |
| ------------ | ------------ |
| userId  | String   |

**METHOD:** PUT  
**BODY:**  
<pre>
     {
	"longitude": "-10.87723",
	"lagitude": "5.8989812"
     }
</pre>


 - ### _updateSchool_
Atualizar escola de um determinado usuário  
 **URL:** /v2/user/{userId}/school  
 **URL Parameters:**  

| Parameter  | Type  |
| ------------ | ------------ |
| userId  | String   |

**METHOD:** PUT  
**BODY:**
<pre>
     {
	"school": "Havard",
     }
</pre>

 - ### _updateAge_
Atualizar escola de um determinado usuário  
**URL:** /v2/user/{userId}/age  
**URL Parameters:**  

| Parameter  | Type  |
| ------------ | ------------ |
| userId  | String   |

**METHOD:** PUT  
**BODY:**
<pre>
     {
	"day": "18",
	"month": "06",
	"year": "2019"
     }
</pre>


 - ### _updateYoutubeChannel_
Atualizar canal do youtube de um determinado usuário  
**URL:** /v2/user/{userId}/youtube  
**URL Parameters:**

| Parameter  | Type  |
| ------------ | ------------ |
| userId  | String   |

**METHOD:** PUT  
**BODY:**
<pre>
     {
	"youtubeChannel": "www.youtube.com/meuvideo2"
     }
</pre>


 - ### _updateInstagramProfile_
Atualizar instagram de um determinado usuário  
**URL:** /v2/user/{userId}/instagram  
**URL Parameters:**  

| Parameter  | Type  |
| ------------ | ------------ |
| userId  | String   |

**METHOD:** PUT  
**BODY:**
<pre>
     {
	"instagramProfile": "liceu.co"
     }
</pre>


 - ### _updateDescription_
Atualizar descrição de um determinado usuário  
**URL:** /v2/user/{userId}/description  
**URL Parameters:**

| Parameter  | Type  |
| ------------ | ------------ |
| userId  | String   |

**METHOD:** PUT  
**BODY:**
<pre>
     {
	"description": "aluno estudioso"
     }
</pre>


 - ### _updateWebsite_
Atualizar website de um determinado usuário  
**URL:** /v2/user/{userId}/website  
**URL Parameters:**

| Parameter  | Type  |
| ------------ | ------------ |
| userId  | String   |

**METHOD:** PUT  
**BODY:**
<pre>
     {
	"website": "www.meusite.com.br"
     }
</pre>



 - ### _updateProducerToBeFollowed_
Inserir produtor da lista de seguidores de um usuário e usuário da lista de pessoas seguindo do produtor  
**URL:** /v2/user/{userId}/followers    
**URL Parameters:**

| Parameter  | Type  |
| ------------ | ------------ |
| userId  | String   |

**METHOD:** PUT  
**BODY:**
<pre>
     {
	"producerId": "bbf0faf94faff222"
     }
</pre>


 - ### _updateProducerToBeUnfollowed_
Remover produtor da lista de seguidores de um usuário e usuário da lista de pessoas seguindo do produtor  
**URL:** /v2/user/{userId}/followers  
**URL Parameters:**

| Parameter  | Type  |
| ------------ | ------------ |
| userId  | String   |

**METHOD:** PUT  
**BODY:**
<pre>
     {
	"producerId": "bbf0faf94faff222"
     }
</pre>

-----------------------

# HTTP STATUS Codes Used
- ### Success Response
200 OK

- ### Error Response
400 BAD REQUEST  
401 UNAUTHORIZED  
500 INTERNAL SERVER ERROR
  
  


[Tippy]
((x) read audio clip volume at runtime)
((x) rigged/animated Tippy asset)
((x) drive mouth by clip volume)
((/) declarative dialogue scenes
  (( ) nicely cancel each other out))


[Village]
(( ) base art assets
  ((/) village)
  ((/) dealership)
  (( ) apartment)
  (( ) highway)
  (( ) factory))

(( ) villager entity
  ((x) animated base body)
  ((x) input component)
  (( ) animal heads)
  (( ) outfit variations)
  ((x) walk ai)
  (( ) ambient speech bubbles)
  (( ) static villagers))

((/) loading areas
  ((x) Portal triggers call a generic load-area fn)
  (( ) any specifics are stored on the area prefab state))
(( ) exits connect with each other, position player)

[UI]
((x) moneycoins)
((x) poor alert)
((/) dialogue UI
  (( ) handle stacked dialogues))

[Rent/game loop]
((x) time and day/month callbacks)
(( ) short on rent endgame)
((/) day/month bills)
(( ) staying out too late penalty)

[Minigames]
((/) sudoku factory
  ((x) viable sudoku game)
  (( ) puzzle variations)
  (( ) background for puzzle view)
  (( ) clock out at 5)
  (( ) area))


[Polish]
(( ) main screen)
(( ) transition effect when changing area)
(( ) town music)

[Bugs]
(( ) fix car bounds)
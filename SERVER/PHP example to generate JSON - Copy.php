<?php
 
// sleep for 2 sec show that the androd swipe refresh will be visible for sometime
sleep(2);
 
// all top 250 movies
$movies = array("The Shawshank Redemption", "The Godfather", "The Godfather: Part II", "The Dark Knight", "Pulp Fiction", "Schindler's List", "12 Angry Men", "The Good, the Bad and the Ugly", "The Lord of the Rings: The Return of the King", "Fight Club", "The Lord of the Rings: The Fellowship of the Ring", "Star Wars: Episode V - The Empire Strikes Back", "Forrest Gump", "Inception", "One Flew Over the Cuckoo's Nest", "The Lord of the Rings: The Two Towers", "Goodfellas", "The Matrix", "Star Wars", "Seven Samurai", "City of God", "Se7en", "The Usual Suspects", "The Silence of the Lambs", "It's a Wonderful Life", "Interstellar", "L�on: The Professional", "Life Is Beautiful", "Once Upon a Time in the West", "Casablanca", "American History X", "Saving Private Ryan", "Spirited Away", "Raiders of the Lost Ark", "City Lights", "Psycho", "Rear Window", "The Intouchables", "Whiplash", "Modern Times", "The Green Mile", "Terminator 2: Judgment Day", "Memento", "The Pianist", "The Departed", "Apocalypse Now", "Gladiator", "Sunset Blvd.", "Dr. Strangelove or: How I Learned to Stop Worrying and Love the Bomb", "Back to the Future", "Alien", "The Prestige", "The Lion King", "The Great Dictator", "The Lives of Others", "Cinema Paradiso", "Django Unchained", "The Shining", "Paths of Glory", "The Dark Knight Rises", "American Beauty", "WALL�E", "North by Northwest", "Aliens", "Citizen Kane", "Grave of the Fireflies", "Vertigo", "M", "Oldboy", "Das Boot", "Am�lie", "Princess Mononoke", "Star Wars: Episode VI - Return of the Jedi", "Once Upon a Time in America", "Toy Story 3", "Reservoir Dogs", "A Clockwork Orange", "Braveheart", "Taxi Driver", "Double Indemnity", "Witness for the Prosecution", "Requiem for a Dream", "To Kill a Mockingbird", "Lawrence of Arabia", "Eternal Sunshine of the Spotless Mind", "Full Metal Jacket", "Bicycle Thieves", "The Sting", "Singin' in the Rain", "Amadeus", "Monty Python and the Holy Grail", "Snatch.", "2001: A Space Odyssey", "For a Few Dollars More", "Rashomon", "L.A. Confidential", "The Kid", "All About Eve", "The Apartment", "Inglourious Basterds", "Toy Story", "The Treasure of the Sierra Madre", "A Separation", "Indiana Jones and the Last Crusade", "Yojimbo", "The Third Man", "Some Like It Hot", "Metropolis", "Batman Begins", "Unforgiven", "Scarface", "Like Stars on Earth", "Raging Bull", "Up", "3 Idiots", "Downfall", "Chinatown", "The Great Escape", "Die Hard", "The Hunt", "On the Waterfront", "Heat", "Mr. Smith Goes to Washington", "Pan's Labyrinth", "Good Will Hunting", "The Bridge on the River Kwai", "My Neighbor Totoro", "Ikiru", "The Seventh Seal", "The Gold Rush", "Ran", "Wild Strawberries", "The General", "Blade Runner", "The Elephant Man", "Lock, Stock and Two Smoking Barrels", "The Secret in Their Eyes", "The Wolf of Wall Street", "Casino", "Gran Torino", "Howl's Moving Castle", "Warrior", "The Big Lebowski", "V for Vendetta", "Rebecca", "The Bandit", "Gone Girl", "The Deer Hunter", "Judgment at Nuremberg", "Cool Hand Luke", "How to Train Your Dragon", "It Happened One Night", "Fargo", "A Beautiful Mind", "Gone with the Wind", "Trainspotting", "Into the Wild", "Rush", "Dial M for Murder", "The Maltese Falcon", "The Sixth Sense", "Mary and Max", "Finding Nemo", "The Thing", "The Wages of Fear", "Hotel Rwanda", "No Country for Old Men", "Incendies", "Rang De Basanti", "Kill Bill: Vol. 1", "Platoon", "Life of Brian", "Butch Cassidy and the Sundance Kid", "Network", "A Wednesday", "Munna Bhai M.B.B.S.", "Touch of Evil", "There Will Be Blood", "12 Years a Slave", "Annie Hall", "The 400 Blows", "Stand by Me", "The Princess Bride", "Persona", "The Grand Budapest Hotel", "Amores Perros", "Ben-Hur", "Diabolique", "In the Name of the Father", "The Grapes of Wrath", "Million Dollar Baby", "Sin City", "Hachi: A Dog's Tale", "Nausica� of the Valley of the Wind", "The Wizard of Oz", "The Best Years of Our Lives", "Gandhi", "The Avengers", "The Bourne Ultimatum", "Donnie Darko", "Shutter Island", "Stalker", "8�", "Guardians of the Galaxy", "Strangers on a Train", "Infernal Affairs", "Twelve Monkeys", "Fanny and Alexander", "Before Sunrise", "Boyhood", "Jaws", "The Imitation Game", "The Battle of Algiers", "The Terminator", "High Noon", "Groundhog Day", "Harry Potter and the Deathly Hallows: Part 2", "Memories of Murder", "The King's Speech", "Ip Man", "Monsters, Inc.", "Notorious", "Rocky", "Dog Day Afternoon", "Barry Lyndon", "La Haine", "The Truman Show", "Who's Afraid of Virginia Woolf?", "A Fistful of Dollars", "Dil Chahta Hai", "The Night of the Hunter", "Pirates of the Caribbean: The Curse of the Black Pearl", "Lagaan: Once Upon a Time in India", "Castle in the Sky", "Jurassic Park", "X-Men: Days of Future Past", "La Strada", "The Help", "Roman Holiday", "Wild Tales", "The Big Sleep", "Spring, Summer, Fall, Winter... and Spring", "Le Samoura�", "Prisoners", "Underground", "The Graduate", "Paris, Texas", "Solaris", "Three Colors: Red", "Papillon");
 
// reading offset from get parameter
$offset = isset($_GET['offset']) && $_GET['offset'] != '' ? $_GET['offset'] : 0;
 
// page limit
$limit = 20;
 
 
$movies_array = array();
 
// loop through page movies
for ($j = $offset; $j < $offset + $limit && $j < sizeof($movies); $j++) {
    $tmp = array();
    $tmp['rank'] = $j + 1;
    $tmp['title'] = $movies[$j];
 
    array_push($movies_array, $tmp);
}
 
// printing json response
echo json_encode($movies_array);
?>
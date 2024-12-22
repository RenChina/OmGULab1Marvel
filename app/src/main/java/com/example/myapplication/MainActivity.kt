package com.example.myapplication

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.marvel_app.data.HeroesRepositoryImplementation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarvelApp()
        }
    }
}

@Composable
fun MarvelApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "heroList") {
        composable("heroList") { HeroListScreen(navController = navController) }
        composable("heroDetails/{heroId}") { backStackEntry ->
            val heroId = backStackEntry.arguments?.getString("heroId")?.toInt() ?: 0
            HeroDetailScreen(heroId = heroId, navController = navController)
        }
    }
}

@Composable
fun HeroListScreen(navController: NavHostController) {
    val repository = HeroesRepositoryImplementation()
    val heroes = remember { mutableStateOf<List<com.example.marvel_app.data.Superhero>>(emptyList()) }

    LaunchedEffect(Unit) {
        val fetchedHeroes = withContext(Dispatchers.IO) { repository.getAllHeroes() }
        heroes.value = fetchedHeroes
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.backgroundmarvel),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AsyncImage(
                model = "https://static.wikia.nocookie.net/marvelcinematicuniverse/images/f/fd/Normal_MarvelStudios2016Logo.png/revision/latest?cb=20180712111124&path-prefix=ru",
                contentDescription = "Marvel Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                    .height(30.dp)
            )

            Text(
                text = "Choose your hero",
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )

            LazyRow(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                items(heroes.value) { hero ->
                    HeroItem(hero = hero) {
                        navController.navigate("heroDetails/${hero.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun HeroItem(hero: com.example.marvel_app.data.Superhero, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(600.dp)
            .width(300.dp)
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            model = hero.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = hero.name,
            style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}

@Composable
fun HeroDetailScreen(heroId: Int, navController: NavHostController) {
    val repository = HeroesRepositoryImplementation()
    val hero = remember { mutableStateOf<com.example.marvel_app.data.Superhero?>(null) }

    LaunchedEffect(heroId) {
        val fetchedHero = withContext(Dispatchers.IO) { repository.getHeroById(heroId.toString()) }
        hero.value = fetchedHero
    }

    hero.value?.let {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AsyncImage(
                model = it.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    textAlign = TextAlign.Left
                )

                Text(
                    text = it.description,
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

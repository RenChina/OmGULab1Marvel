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
                items(heroesList) { hero ->
                    HeroItem(hero = hero) {
                        navController.navigate("heroDetails/${hero.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun HeroItem(hero: Hero, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(600.dp)
            .width(300.dp)
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            model = hero.url,
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
    val hero = heroesList.find { it.id == heroId }
    hero?.let {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AsyncImage(
                model = hero.url,
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
                    text = hero.name,
                    style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    textAlign = TextAlign.Left
                )

                Text(
                    text = hero.description,
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
data class Hero(
    val id: Int,
    val name: String,
    val description: String,
    val url: String
)

val heroesList = listOf(
    Hero(1, "Deadpool", "Please don't make the super suit green...or animated!",  "https://i.pinimg.com/564x/6f/3a/a5/6f3aa5c8784e60563d787bceab7c8253.jpg"),
    Hero(2, "Iron Man", "I'M IRON MAN",  "https://i.pinimg.com/564x/64/bc/f8/64bcf8a9ebacf999b23f0248bfa5c69a.jpg"),
    Hero(3, "Spider Man", "In iron suit",  "https://i.pinimg.com/564x/aa/0f/31/aa0f316bc2b6571a1b8c20d4ad6766a9.jpg")
)

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MarvelApp()
}



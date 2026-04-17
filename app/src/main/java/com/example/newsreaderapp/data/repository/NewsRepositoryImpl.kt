package com.example.newsreaderapp.data.repository

import com.example.newsreaderapp.data.local.ArticleDao
import com.example.newsreaderapp.data.local.ArticleEntity
import com.example.newsreaderapp.domain.Article
import com.example.newsreaderapp.domain.repository.NewsRepository
import com.example.newsreaderapp.domain.repository.Resource
import io.ktor.client.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NewsRepositoryImpl(
    private val client: HttpClient,
    private val dao: ArticleDao
) : NewsRepository {

    // Menghilangkan "/g/" agar gambar muncul BERWARNA sesuai konteks balapan
    private val beritaPool = listOf(
        ArticleEntity(
            1, 
            "Gemerlap Start F1: Persaingan di GP Jepang 2024", 
            "Seluruh pembalap memacu jet darat mereka saat lampu hijau menyala, berebut posisi terdepan di tengah kepungan sponsor global.", 
            "https://loremflickr.com/800/500/f1,race,start/all", 
            "https://www.formula1.com"
        ),
        ArticleEntity(
            2, 
            "Duel Sengit Red Bull Racing di Barisan Depan", 
            "Max Verstappen menunjukkan dominasinya di lintasan, mengamankan posisi terdepan dari kejaran pesaing terdekatnya.", 
            "https://loremflickr.com/800/500/f1,redbull/all", 
            "https://www.formula1.com"
        ),
        ArticleEntity(
            3, 
            "Tantangan Ekstrem di Sirkuit Jalan Raya", 
            "Kecepatan tinggi di tengah pembatas tembok yang sangat rapat memberikan sensasi balap yang memacu adrenalin bagi para pembalap.", 
            "https://loremflickr.com/800/500/f1,circuit/all", 
            "https://www.formula1.com"
        ),
        ArticleEntity(
            4, 
            "MotoGP Mandalika: Kebanggaan Indonesia di Mata Dunia", 
            "Sirkuit Pertamina Mandalika sukses menggelar balapan kelas dunia dengan antusiasme luar biasa dari para penggemar racing.", 
            "https://loremflickr.com/800/500/motogp,mandalika/all", 
            "https://www.motogp.com"
        ),
        ArticleEntity(
            5, 
            "Aksi Rombongan Rider MotoGP di Tikungan Tajam", 
            "Teknik 'elbow down' saat melahap tikungan dalam kecepatan tinggi menjadi pemandangan paling ikonik di setiap seri balapan.", 
            "https://loremflickr.com/800/500/motogp,racing/all",
            "https://www.motogp.com"
        )
    )

    override fun getArticles(): Flow<Resource<List<Article>>> = flow {
        emit(Resource.Loading())

        val localArticles = dao.getAllArticles().map { it.toArticle() }
        emit(Resource.Loading(data = localArticles))

        try {
            delay(1000)
            
            val updatedList = beritaPool

            dao.clearArticles()
            dao.insertArticles(updatedList)

            val finalArticles = dao.getAllArticles().map { it.toArticle() }
            emit(Resource.Success(finalArticles))

        } catch (e: Exception) {
            emit(Resource.Error(
                message = "Gagal memuat berita terbaru. Menampilkan data luring.",
                data = localArticles
            ))
        }
    }

    override suspend fun getArticleById(id: Int): Article? {
        return dao.getArticleById(id)?.toArticle()
    }

    private fun ArticleEntity.toArticle() = Article(
        id = id,
        title = title,
        description = content,
        imageUrl = imageUrl,
        url = url
    )
}

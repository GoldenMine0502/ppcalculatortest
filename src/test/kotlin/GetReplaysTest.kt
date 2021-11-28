import com.google.gson.Gson
import kr.goldenmine.ppcalculator.osuapi.OsuReplayResponse
import kr.goldenmine.ppcalculator.osuapi.OsuScore
import kr.goldenmine.ppcalculator.osuapi.RetrofitClient
import kr.goldenmine.ppcalculator.osuapi.RetrofitService
import kr.goldenmine.ppcalculator.util.LzmaCompressor
import lzma.sdk.lzma.Decoder
import lzma.streams.LzmaInputStream
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*

fun main() {
    val service = RetrofitClient.instance.create(RetrofitService::class.java)
    val apiKey = File("APIKey.txt").readText()

    service.requestScoreRanking(apiKey, 1805627, limit=10).enqueue(object : Callback<Array<OsuScore>> {
        override fun onFailure(call: Call<Array<OsuScore>>, t: Throwable) {
            t.printStackTrace()
        }

        //https://osu.ppy.sh/scores/osu/3106051627/download

        override fun onResponse(call: Call<Array<OsuScore>>, response: Response<Array<OsuScore>>) {
            if(response.isSuccessful) {
                println("succeed getting scores")
                response.body()?.iterator()?.forEach {
                    val fileName = "replay ${it.scoreId}.osr"
                    println("downloading $fileName")

                    service.requestOsuReplayFile(apiKey, it.scoreId).enqueue(object : Callback<OsuReplayResponse> {
                        override fun onFailure(call: Call<OsuReplayResponse>, t: Throwable) {
                            t.printStackTrace()
                        }

                        override fun onResponse(call: Call<OsuReplayResponse>, response: Response<OsuReplayResponse>) {
                            if(response.isSuccessful) {
                                response.body()?.apply {
                                    println(this.content)

                                    val folder = File("replays")
                                    folder.mkdirs()

                                    val file = File("replays/$fileName")
                                    if(!file.exists()) file.createNewFile()

                                    val inputStream = LzmaInputStream(ByteArrayInputStream(decodeBase64(content)), Decoder())
                                    writeFile(inputStream, fileName)
//                                    val writer = FileWriter(file)
//                                    val fileOutputStream = FileOutputStream(file)
//                                    fileOutputStream.write(decodeBase64(content))
//                                    fileOutputStream.close()
//                                    val compressor = LzmaCompressor(Paths.get())
//                                    Gson().toJson(this, writer)
//                                    writer.close()

//                                    val decoded = decodeBase64(this.content)
//                                    println(decoded.size)
//                                    println(String(decoded))
//                                    val inputStream = LzmaInputStream(ByteArrayInputStream(decoded), Decoder())
//
//                                    writeFile(inputStream, fileName)
                                    println("downloaded $fileName")
                                }
                            } else {
                                println("failed to get replay")
                            }
                        }

                    })

                    Thread.sleep(2000L)
                }
            } else {
                println("failed to get scores")
            }
        }
    })
}

fun decodeBase64(encoded: String): ByteArray {
    val decoder = Base64.getDecoder()

    return decoder.decode(encoded)
}

fun writeFile(inputStream: InputStream, fileName: String) {
    val folder = File("replays")
    folder.mkdirs()

    val file = File("replays/$fileName")
    if(!file.exists()) file.createNewFile()

    val outputStream = BufferedOutputStream(FileOutputStream(file))

    inputStream.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }

//    val buffer = ByteArray(4096)
//
//    val outputStream = BufferedOutputStream(FileOutputStream(file))
//    var length: Int
//    while (true) {
//        length = inputStream.read(buffer)
//        if(length <= 0) break
//
//        outputStream.write(buffer, 0, length)
//    }
}
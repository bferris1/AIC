/**
 * Created by Ben on 13/08/2017.
 * Repository for Favorites
 */

public class FavoritesRepository {

    private FavoriteDao mFavoriteDao;
    private AppExecutors mAppExecutors;
    private Webservice mWebservice;
    private OkHttpClient mHttpClient;
    private SharedPreferences mSharedPreferences;
    private static final String TAG = "FAVORITES_REPOSITORY";

    @Inject
    public FavoritesRepository(FavoriteDao favoriteDao, AppExecutors appExecutors, Webservice webservice, OkHttpClient httpClient, SharedPreferences sharedPreferences) {
        mFavoriteDao = favoriteDao;
        mAppExecutors = appExecutors;
        mWebservice = webservice;
        mHttpClient = httpClient;
        mSharedPreferences = sharedPreferences;
    }


    // here you can see lambda expressions and AICs used side-by-side
    // lambda expressions can only be used for functional interfaces, interfaces with only one method
    // in this case, the Runnable interface
    // the FavoriteTransactionTask abstract class (which implements Runnable) has multiple abstract methods
    // so it is instantiated as an anonymous inner class

    public void addFavorite(MenuItem item) {
        Favorite favorite = new Favorite(item.getName(), UUID.randomUUID().toString(), item.getId(), item.isVegetarian());
        mAppExecutors.diskIO().execute(() -> mFavoriteDao.insertFavorites(favorite));
        if (mSharedPreferences.getBoolean("logged_in", false))
            mAppExecutors.networkIO().execute(new FavoriteTransactionTask<ResponseBody>(mHttpClient, mSharedPreferences) {
                @Override
                public Call<ResponseBody> getCall(@Nullable String ticket) {
                    return mWebservice.addFavorite(favorite, ticket);
                }

                @Override
                public void onSuccess(Response<ResponseBody> response) {
                    Log.d(TAG, "onSuccess: " + response);
                    updateFavoritesFromWeb();
                }
            });
    }

    public void removeFavorite(MenuItem item) {
        if (mSharedPreferences.getBoolean("logged_in", false))
            mAppExecutors.diskIO().execute(() -> {
                Favorite favorite = mFavoriteDao.getFavoriteByItemId(item.getId());
                Log.d(TAG, "removeFavorite: deleting favorite" + favorite + " " + favorite.favoriteId);
                mAppExecutors.networkIO().execute(new FavoriteTransactionTask<ResponseBody>(mHttpClient, mSharedPreferences) {
                    @Override
                    public Call<ResponseBody> getCall(@Nullable String ticket) {
                        return mWebservice.deleteFavorite(favorite.favoriteId, ticket);
                    }

                    @Override
                    public void onSuccess(Response<ResponseBody> response) {
                        Log.d(TAG, "onSuccess: " + response);
                    }
                });
                mFavoriteDao.deleteByItemID(item.getId());
            });
        else
            mAppExecutors.diskIO().execute(() -> mFavoriteDao.deleteByItemID(item.getId()));

    }

    public void clearLocalFavorites() {
        mAppExecutors.diskIO().execute(() -> mFavoriteDao.deleteAll());
    }
}

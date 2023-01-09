package online.daliyq.ui.profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import online.daliyq.AuthManager
import online.daliyq.R
import online.daliyq.api.response.User
import online.daliyq.databinding.FragmentProfileBinding
import online.daliyq.db.entity.UserEntity
import online.daliyq.ui.base.BaseFragment
import java.net.ConnectException

class ProfileFrgment : BaseFragment() {
    companion object{
        const val ARG_UID = "uid"
    }

    var _binding: FragmentProfileBinding? = null
    val binding get() = _binding!!
    val uid: String by lazy {
        requireArguments().getString(ARG_UID)!!
    }

    var adapter: UserAnswerAdapter? = null

    val useranswerFlow = Pager(PagingConfig(pageSize = 5)){
        UserAnswerPagingSource(api, uid)
    }.flow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val oldUserEntity = db.getUserDao().get(uid)
            if(oldUserEntity != null){
                setupProfile(oldUserEntity)
            }

            val user: User
            try {
                val userResponse = api.getUser(uid)
                if(!userResponse.isSuccessful){
                    return@launch
                }
                
                user = userResponse.body()!!
            }catch (e: ConnectException){
                return@launch
            }

            val newUserEntity = UserEntity(
                user.id,
                user.name,
                user.description,
                user.photo,
                user.answerCount,
                user.followerCount,
                user.followingCount,
                user.isFollowing ?: false,
                user.updatedAt
            )

            if(oldUserEntity == null){
                db.getUserDao().insert(newUserEntity)
                setupProfile(newUserEntity)
            } else if(oldUserEntity != newUserEntity){
                db.getUserDao().update(newUserEntity)
                setupProfile(newUserEntity)
            }
        }

        lifecycleScope.launch{
            adapter = UserAnswerAdapter(requireContext())
            binding.pager.adapter = adapter

            useranswerFlow.collectLatest {
                adapter?.submitData(it)
            }
        }
    }
    
    fun setupProfile(user: UserEntity){
        binding.name.text = user.name
        binding.description.text = user.description
        binding.answerCount.text = user.answerCount.toString()
        binding.followerCount.text = user.followerCount.toString()
        binding.followingCount.text = user.followingCount.toString()
        user.photo.let {
            binding.photo.load(it){
                placeholder(R.drawable.ph_user)
                error(R.drawable.ph_user)
                transformations(CircleCropTransformation())
            }
        }
        
        when{
            user.id == AuthManager.uid -> {
                binding.followButton.setText(R.string.me)
                binding.followButton.isEnabled = false
            }
            user.isFollowing ->{
                binding.followButton.setText(R.string.unfollow)
                binding.followButton.isEnabled = true
                binding.followButton.backgroundTintList =
                    ColorStateList.valueOf(
                        MaterialColors.getColor(
                            binding.followButton,
                            R.attr.colorUnfollowButton
                        )
                    )
                binding.followButton.setOnClickListener { 
                    lifecycleScope.launch { 
                        val response = api.unfollow(user.id)
                        if(response.isSuccessful){
                            setupProfile(user.copy(isFollowing = false))
                        }
                    }
                }
            }
            else ->{
                binding.followButton.setText(R.string.follow)
                binding.followButton.isEnabled = true
                binding.followButton.backgroundTintList =
                    ColorStateList.valueOf(
                        MaterialColors.getColor(
                            binding.followButton,
                            R.attr.colorFollowButton
                        )
                    )
                binding.followButton.setOnClickListener {
                    lifecycleScope.launch {
                        val response = api.follow(user.id)
                        if(response.isSuccessful){
                            setupProfile(user.copy(isFollowing = true))
                        }
                    }
                }
            }
        }
    }
}
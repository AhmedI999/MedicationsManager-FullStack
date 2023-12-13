const UserProfile = ({ name, age, ...props }) => {
    return (
        <div>
            <h1>{name}</h1>
            <p>{age}</p>
            {props.children}
        </div>
    )
}

export default UserProfile;
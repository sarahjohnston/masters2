namespace ApiReviews.Models
{
    using System;
    using System.Data.Entity;
    using System.ComponentModel.DataAnnotations.Schema;
    using System.Linq;

    public partial class ReviewsModel : DbContext
    {
        public ReviewsModel()
            : base("name=ReviewsModel")
        {
        }

        public virtual DbSet<place> places { get; set; }
        public virtual DbSet<review> reviews { get; set; }
        public virtual DbSet<user> users { get; set; }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            modelBuilder.Entity<place>()
                .Property(e => e.PlaceName)
                .IsUnicode(false);

            modelBuilder.Entity<review>()
                .Property(e => e.Headline)
                .IsUnicode(false);

            modelBuilder.Entity<review>()
                .Property(e => e.ReviewText)
                .IsUnicode(false);

            modelBuilder.Entity<user>()
                .Property(e => e.UserName)
                .IsUnicode(false);
        }
    }
}

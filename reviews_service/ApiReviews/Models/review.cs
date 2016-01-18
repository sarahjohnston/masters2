namespace ApiReviews.Models
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.DataAnnotations;
    using System.ComponentModel.DataAnnotations.Schema;
    using System.Data.Entity.Spatial;

    [Table("sarahjdb.reviews")]
    public partial class review
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; }

        public int? PlaceId { get; set; }

        public int? Rating { get; set; }

        [StringLength(300)]
        public string Headline { get; set; }

        [StringLength(1200)]
        public string ReviewText { get; set; }

        public int? ReviewerId { get; set; }

        [Column(TypeName = "date")]
        public DateTime? Date { get; set; }
    }
}
